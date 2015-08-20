package ca.uqam.latece.rbp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCLExpression;
import org.eclipse.ocl.ecore.utilities.ToStringVisitor;
import org.eclipse.ocl.helper.OCLHelper;

import cbpmn.FlowNode;
import cbpmn.ProcessModel;
import cbpmn.util.CbpmnReverseIterator;
import cbpmn.util.CbpmnSimpleIterator;
import cbpmn.util.CbpmnSimulateIterator;

public class Main {

	public static void main(String[] args) {
		Main instance = new Main();
		instance.run();
	}
	
	public void run(){
		CBPMNModel processModelLoader = new CBPMNModel("amazon_sd/AmazonSDProcModel.cbpmn");
		ECoreModel informationalModelLoader = new ECoreModel("amazon_sd/AmazonInformational.ecore");
		EPackage informationalModel = informationalModelLoader.getEPackage();
		ProcessModel processModel = processModelLoader.getProcessModel(); 
		/*Iterator<FlowNode> it = processModel.iterator(new CbpmnReverseIterator(), processModel.getMainBranch().getLastNode());
		while(it.hasNext()){
			FlowNode current = it.next();
			System.out.println(current.eClass().getName()+": "+current.getName());
		}*/
		
		GlueModel glueModel;
		try{ 
			glueModel = new GlueModel(processModel, informationalModel);
			glueModel.save();
			
			//GlueModelNode node = glueModel.getNodeByName("CheckOrder");
			for(GlueModelNode node : glueModel.getNodes()){
				System.out.println("====="+node.getName()+"=====");
				System.out.println("[PRECONDITIONS] "+node.getAllReferredVariablesByOCL(GlueModelNode.ConditionType.PRECOND));
				System.out.println("[POSTCONDITIONS] "+node.getAllReferredVariablesByOCL(GlueModelNode.ConditionType.POSTCOND));
				System.out.println("[MODIFIES] "+node.getAllReferredVariablesByOCL(GlueModelNode.ConditionType.MODIFIES));
			}
			
			List<String> sliceCriteria = new ArrayList<String>();
			sliceCriteria.add("order.status");
			Slicer slicer = new Slicer(processModel, glueModel, processModel.getMainBranch().getLastNode(), sliceCriteria);
			Set<FlowNode> slice = slicer.doSlice();
			System.out.println("======= SLICE ======");
			for(FlowNode node:slice){
				System.out.println("["+node.eClass().getName()+"] "+node.getName());
			}
						
		} catch (ParserException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}	
	}
}
