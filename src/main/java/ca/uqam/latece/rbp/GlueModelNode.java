package ca.uqam.latece.rbp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.OCLExpression;

import cbpmn.Activity;
import cbpmn.Branch;
import cbpmn.DataObjectReference;
import cbpmn.DecisionGateway;
import cbpmn.Event;
import cbpmn.FlowNode;

public class GlueModelNode{
	private EClass eclass;
	private FlowNode flowNode;
	private Map<String, SlicingOCLSubject> preConditions;
	private Map<String, SlicingOCLSubject> postConditions;
	private Map<String, SlicingOCLSubject> modifies;
	private Map<String, Branch> conditionsForBranches;
	private EPackage informationalModel;
	
	private static int nodeCount = 0;
	
	private Map<String, OCLExpression> cumulativeState;
	
	public enum ConditionType{
		PRECOND, POSTCOND, MODIFIES
	}
	
	public GlueModelNode(FlowNode flowNode, EPackage informationalModel) throws ParserException{
		this.eclass = EcoreFactory.eINSTANCE.createEClass();
		String name; 
		if(flowNode.getName()!=null && flowNode.getName()!="")
			name =  convertSpaceSeparatedStringToCamelCase(flowNode.getName()) ;
		else
			name =  flowNode.eClass().getName()+nodeCount ;
		this.eclass.setName(name);
		this.flowNode = flowNode;
		this.informationalModel = informationalModel;
		this.preConditions = new Hashtable<String, SlicingOCLSubject>();
		this.postConditions = new Hashtable<String, SlicingOCLSubject>();
		this.modifies = new Hashtable<String, SlicingOCLSubject>();
		this.cumulativeState = new Hashtable<String, OCLExpression>();
		this.conditionsForBranches = new Hashtable<String, Branch>();
		
		this.createNodeInputOutputReferences();
		
		this.specificFlowNodeHandling();
		
		nodeCount++;
	}
	
	private void specificFlowNodeHandling() throws ParserException{
		if(this.flowNode instanceof Activity)
			handleActivity();
		if(this.flowNode instanceof Event) 
			handleEvent();
		if(this.flowNode instanceof DecisionGateway)
			handleDecisionGateway();
	}
	
	private void createNodeInputOutputReferences(){
		List<String> inputNames = new ArrayList<String>();
		for(DataObjectReference input : flowNode.getInputs()){
			EReference dataRef = EcoreFactory.eINSTANCE.createEReference();
			dataRef.setName(input.getName());
			//dataRef.setEType(input.getDataObjectClass());
			dataRef.setEType( informationalModel.getEClassifier(input.getDataObjectClass().getName()) );
			dataRef.setUpperBound(input.getHigherBound());
			dataRef.setLowerBound(input.getLowerBound());
			eclass.getEStructuralFeatures().add(dataRef);
			inputNames.add(input.getName());
		}
		if(flowNode instanceof Activity){
			Activity activity = (Activity)flowNode;
			for(DataObjectReference output : activity.getOutputs())
				if(!inputNames.contains(output.getName())){
					EReference dataRef = EcoreFactory.eINSTANCE.createEReference();
					dataRef.setName(output.getName());
					//dataRef.setEType(output.getDataObjectClass());
					dataRef.setEType( informationalModel.getEClassifier(output.getDataObjectClass().getName()) );
					dataRef.setUpperBound(output.getHigherBound());
					dataRef.setLowerBound(output.getLowerBound());
					eclass.getEStructuralFeatures().add(dataRef);
				}
		}
	}
	
	private void handleActivity() throws ParserException{
		Activity activity = (Activity)flowNode;
		for(cbpmn.OCLConstraint constraint : activity.getPreConditions()){
			SlicingOCLSubject parsedConstraint = new SlicingOCLSubject(constraint, eclass);
			if(parsedConstraint.getOclExpression()!=null)
				preConditions.put(this.getName()+"::"+constraint.getConstraintName(), parsedConstraint);
		}
		for(cbpmn.OCLConstraint constraint : activity.getPostConditions()){
			SlicingOCLSubject parsedConstraint = new SlicingOCLSubject(constraint, eclass);
			if(parsedConstraint.getOclExpression()!=null)
				postConditions.put(this.getName()+"::"+constraint.getConstraintName(), parsedConstraint);
		}
		for(cbpmn.OCLConstraint constraint : activity.getInvariabilityClauses()){
			SlicingOCLSubject parsedConstraint = new SlicingOCLSubject(constraint, eclass, true);
			if(parsedConstraint.getOclExpression()!=null)
				modifies.put(this.getName()+"::"+constraint.getConstraintName(), parsedConstraint);
		}
	}
	
	private void handleEvent() throws ParserException{
		Event event = (Event)flowNode;
		cbpmn.OCLConstraint constraint = event.getTrigger();
		if(constraint!=null){
			SlicingOCLSubject parsedConstraint = new SlicingOCLSubject(constraint, eclass);
			if(parsedConstraint.getOclExpression()!=null)
				preConditions.put(this.getName()+"::"+constraint.getConstraintName(), parsedConstraint);
		}
	}
	
	private void handleDecisionGateway() throws ParserException{
		DecisionGateway gateway = (DecisionGateway)flowNode;
		int count =0;
		for(Branch branch: gateway.getBranches()){
			for(cbpmn.OCLConstraint constraint:branch.getEntryConditions()){
				if(constraint!=null){
					SlicingOCLSubject parsedConstraint = new SlicingOCLSubject(constraint, eclass);
					if(parsedConstraint.getOclExpression()!=null){
						String constraintId = this.getName()+"::"+count+"::"+constraint.getConstraintName();
						preConditions.put(constraintId, parsedConstraint);
						conditionsForBranches.put(constraintId, branch);
						count++;
					}
				}
			}
		}
	}
	
	public EClass getEclass(){
		return this.eclass;
	}

	public Map<String, SlicingOCLSubject> getPreConditions() {
		return preConditions;
	}

	public Map<String, SlicingOCLSubject> getPostConditions() {
		return postConditions;
	}

	public Map<String, SlicingOCLSubject> getModifies() {
		return modifies;
	}
	
	private String convertSpaceSeparatedStringToCamelCase(String string){
		StringBuilder className = new StringBuilder();
		String[] parts = string.split(" ");
		for(String part : parts){
			className.append(part.substring(0, 1).toUpperCase());
			className.append(part.substring(1).toLowerCase());
		}
		return className.toString();
	}
	
	public SlicingOCLSubject getConditionByName(ConditionType type, String name){
		Map<String, SlicingOCLSubject> lookupSet = null;
		switch(type){
		case PRECOND:  lookupSet = this.preConditions; break;
		case POSTCOND: lookupSet = this.postConditions; break;
		case MODIFIES: lookupSet = this.modifies; break;
		default: return null;
		}
		for(Map.Entry<String, SlicingOCLSubject> entry : lookupSet.entrySet()){
			if(entry.getKey().compareTo(name)==0) return entry.getValue();
		}
		return null;
	}
	
	public FlowNode getFlowNode() {
		return flowNode;
	}
	
	public String getName(){
		return this.getEclass().getName();
	}
	
	public Set<String> getAllReferredVariablesByOCL(){
		Set<String> result = new HashSet<String>();
		result.addAll(this.getAllReferredVariablesByOCL(ConditionType.PRECOND));
		result.addAll(this.getAllReferredVariablesByOCL(ConditionType.POSTCOND));
		result.addAll(this.getAllReferredVariablesByOCL(ConditionType.MODIFIES));
		return result;
	}
	
	public Set<String> getAllReferredVariablesByOCL(ConditionType forType){
		Set<String> result = new HashSet<String>();
		Set<Map.Entry<String, SlicingOCLSubject>> entrySet;
		switch(forType){
		case PRECOND: entrySet = this.preConditions.entrySet(); break;
		case POSTCOND: entrySet = this.postConditions.entrySet(); break;
		case MODIFIES: entrySet = this.modifies.entrySet(); break;
		default: return null;
		}
		for(Map.Entry<String, SlicingOCLSubject> entry:entrySet){
			result.addAll(entry.getValue().getReferredVariables());
		}
		return result;
	}
	
	public Set<String> getAllReferredVariablesByOCL(ConditionType forType, Branch forBranch){
		Set<String> result = new HashSet<String>();
		Set<Map.Entry<String, SlicingOCLSubject>> entrySet;
		switch(forType){
		case PRECOND: entrySet = this.preConditions.entrySet(); break;
		case POSTCOND: entrySet = this.postConditions.entrySet(); break;
		case MODIFIES: entrySet = this.modifies.entrySet(); break;
		default: return null;
		}
		for(Map.Entry<String, SlicingOCLSubject> entry:entrySet){
			Branch foundBranch = this.conditionsForBranches.get(entry.getKey());
			if(foundBranch!=null && foundBranch==forBranch){
				result.addAll(entry.getValue().getReferredVariables());
			}
		}
		return result;
	}
}
