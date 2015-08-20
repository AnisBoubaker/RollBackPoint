package ca.uqam.latece.rbp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbpmn.Branch;
import cbpmn.FlowNode;
import cbpmn.ProcessModel;
import cbpmn.SplitGateway;
import cbpmn.util.CbpmnReverseIterator;

public class Slicer {
	private ProcessModel processModel; 
	private GlueModel glueModel;
	private FlowNode sliceFrom;
	private List<String> slicingCriterias;
	private Set<FlowNode> inSlice;
	private Set<Branch> branchesToSlice;
	private Map<FlowNode, List<String>> referenced;
	private Map<FlowNode, List<String>> defined;
	private Map<FlowNode, List<String>> relevant;
	private Map<SplitGateway, FlowNode> gatewayPreviousNode;
	
	public Slicer(ProcessModel processModel, GlueModel glueModel, FlowNode sliceFrom, List<String> slicingCriterias){
		this.processModel = processModel;
		this.glueModel = glueModel;
		this.sliceFrom = sliceFrom;
		this.slicingCriterias = new ArrayList<String>();
		this.slicingCriterias.addAll(slicingCriterias);
		this.inSlice = new HashSet<FlowNode>();
		this.branchesToSlice = new HashSet<Branch>();
		this.referenced = new HashMap<FlowNode, List<String>>();
		this.relevant = new HashMap<FlowNode, List<String>>();
		this.defined = new HashMap<FlowNode, List<String>>();
		this.gatewayPreviousNode = new HashMap<SplitGateway, FlowNode>();
	}
	
	public Set<FlowNode> doSlice(){
		doSlice(sliceFrom, this.slicingCriterias);
		for(Branch branch: this.branchesToSlice){
			SplitGateway gateway = branch.getGateway();
			List<String> gatewayCriteria = this.getGatewayCriteria(branch);
			System.out.println("--------------------------");
			System.out.println("Slicing for gateway: "+gateway.getName());
			System.out.println("For criteria: "+gatewayCriteria);
			System.out.println("--------------------------");
			doSlice(gateway, gatewayCriteria);
		}
		return this.inSlice;
	}
	
	private List<String> getGatewayCriteria(Branch branch){
		SplitGateway gateway = branch.getGateway();
		List<String> gatewayCriteria = new ArrayList<String>();
		if(branch.isDefault()){//Default branch: consider variables of all other branches
			for(Branch gatewayBranch: gateway.getBranches()){
				gatewayCriteria.addAll(glueModel.getNodeByFlowNode(gateway).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.PRECOND, gatewayBranch));
				gatewayCriteria.addAll(glueModel.getNodeByFlowNode(gateway).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.POSTCOND, gatewayBranch));
			}
		} else {
			gatewayCriteria.addAll(glueModel.getNodeByFlowNode(gateway).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.PRECOND, branch));
			gatewayCriteria.addAll(glueModel.getNodeByFlowNode(gateway).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.POSTCOND, branch));
		}
		return gatewayCriteria;
	}
	
	private Set<FlowNode> doSlice(FlowNode node, List<String> slicingCriterias){
		//Initialize maps...
		this.referenced.clear();
		this.defined.clear();
		this.relevant.clear();
		this.gatewayPreviousNode.clear();
		
		
		Iterator<FlowNode> iterator = this.processModel.iterator(new CbpmnReverseIterator(), node);
		this.inSlice.add(node);
		FlowNode previousNode = node;
		while(iterator.hasNext()){
			FlowNode currentNode = iterator.next();
			System.out.println("Analysing "+currentNode.eClass().getName()+"::"+currentNode.getName());
			
			if(currentNode instanceof SplitGateway && currentNode!=node){
				SplitGateway gateway = (SplitGateway)currentNode;
				previousNode = this.gatewayPreviousNode.get(currentNode);
				computeGatewayRelevantNodes(gateway);
			}
			
			if(!(currentNode instanceof SplitGateway) && currentNode.getBranch()!=previousNode.getBranch()){
				//We switched branches, reset the previous node.
				SplitGateway branchGateway = currentNode.getBranch().getGateway();
				if(this.gatewayPreviousNode.containsKey(branchGateway)){
					previousNode = this.gatewayPreviousNode.get(branchGateway);
				}
			}
			
			this.fillOCLVariablesMaps(currentNode, previousNode);
			
			if(!(currentNode instanceof SplitGateway) && currentNode.getBranch()!=previousNode.getBranch()){
				SplitGateway branchGateway = currentNode.getBranch().getGateway();
				if(!this.gatewayPreviousNode.containsKey(branchGateway)){
					this.gatewayPreviousNode.put(branchGateway, previousNode);
				}
				if(this.inSlice.contains(currentNode) && !this.branchesToSlice.contains(currentNode.getBranch())){
					this.inSlice.add(branchGateway);
					this.branchesToSlice.add(currentNode.getBranch());
				}
			}			
			previousNode = currentNode;
		}//while
		
		return this.inSlice;
	}
	
	private void computeGatewayRelevantNodes(SplitGateway gateway){
		List<String> relevantVariables = new ArrayList<String>();
		for(Branch branch: gateway.getBranches()){
			relevantVariables.addAll(this.relevant.get(branch.getFirstNode()));
		}
		this.relevant.put(gateway, relevantVariables);
	}
	
	private void fillOCLVariablesMaps(FlowNode currentNode, FlowNode previousNode){
		System.out.println("     PreviousNode ="+previousNode.eClass().getName()+"::"+previousNode.getName());
		List<String> referencedVariables = new ArrayList<String>(); 
		referencedVariables.addAll(
			glueModel.getNodeByFlowNode(currentNode).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.PRECOND)
			);
		referencedVariables.addAll(
			glueModel.getNodeByFlowNode(currentNode).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.POSTCOND)
			);
		
		List<String> definedVariables = new ArrayList<String>();
		definedVariables.addAll(
			glueModel.getNodeByFlowNode(currentNode).getAllReferredVariablesByOCL(GlueModelNode.ConditionType.MODIFIES)
			);
		//Remove from referenced variables the one that were defined...
		List<String> toBeRemovedFromReferenced = new ArrayList<String>();
		for(String defined : definedVariables){
			for(String referenced : referencedVariables){
				if(defined.compareTo(referenced)==0) 
					toBeRemovedFromReferenced.add(referenced);
			}
		}
		referencedVariables.removeAll(toBeRemovedFromReferenced);
		
		List<String> relevantVariables;
		if(currentNode==previousNode){ //This is the starting node...
			relevantVariables = this.slicingCriterias;
			this.inSlice.add(currentNode);
		} else {
			relevantVariables = new ArrayList<String>();
			relevantVariables.addAll(this.relevant.get(previousNode));
			relevantVariables.removeAll(definedVariables);
			if(isNodeIncludedInSlice(this.relevant.get(previousNode), definedVariables)){
				relevantVariables.addAll(referencedVariables);
				this.inSlice.add(currentNode);
				System.out.println("     Adding node "+currentNode.getName()+" to slice.");
			}
		}
		this.referenced.put(currentNode, referencedVariables);
		this.defined.put(currentNode, definedVariables);
		this.relevant.put(currentNode, relevantVariables);
	}
	
	private boolean isNodeIncludedInSlice(List<String> previousNodeRelevant, List<String> currentNodeDefined){
		for(String defined : currentNodeDefined){
			for(String previousRel : previousNodeRelevant){
				if(defined.compareTo(previousRel)==0)
					return true;
			}
		}
		return false;
	}
}
