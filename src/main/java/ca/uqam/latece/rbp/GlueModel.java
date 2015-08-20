package ca.uqam.latece.rbp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.ocl.ParserException;

import ca.uqam.latece.rbp.util.BiMap;
import cbpmn.Activity;
import cbpmn.FlowNode;
import cbpmn.ProcessModel;
import cbpmn.util.CbpmnSimpleIterator;

public class GlueModel {

	private ProcessModel processModel;
	private EPackage informationalModel;
	private EPackage gluePackage;
	private EcoreFactory ecoreFactory;
	private Map<String, GlueModelNode> nodesByName;
	private BiMap<GlueModelNode, FlowNode> glueNodeFlowNodeMap;
	
	public GlueModel(ProcessModel processModel, EPackage informationalModal) 
			throws ParserException {
		this.processModel = processModel;
		this.informationalModel = informationalModal;
		
		ecoreFactory = EcoreFactory.eINSTANCE;
		this.gluePackage = ecoreFactory.createEPackage();
		
		String name = this.processModel.getName()+informationalModal.getName()+"glue";
		gluePackage.setName(name);
		gluePackage.setNsPrefix(name);
		gluePackage.setNsURI(informationalModal.getNsURI()+".glue");
		
		nodesByName = new HashMap<String,GlueModelNode>();
		glueNodeFlowNodeMap = new BiMap<GlueModelNode, FlowNode>();
		
		constructGlueModel();
	}
	
	private void constructGlueModel() throws ParserException {
		EClass abstractNode = createTheAbstractNode();
		gluePackage.getEClassifiers().add(abstractNode);
		
		for(Iterator<FlowNode> it = processModel.iterator(new CbpmnSimpleIterator()); it.hasNext() ; ){
			FlowNode flowNode = it.next();
			GlueModelNode node = createNodeClassForFlowNode(flowNode, abstractNode);
			this.nodesByName.put(node.getName(), node);
			this.glueNodeFlowNodeMap.put(node, flowNode);
			gluePackage.getEClassifiers().add(node.getEclass());
		}
		
	}
	
	private GlueModelNode createNodeClassForFlowNode(FlowNode flowNode, EClass abstractNode) throws ParserException{
		GlueModelNode node = new GlueModelNode(flowNode, informationalModel);
		EClass eclass = node.getEclass();
		
		eclass.getESuperTypes().add(abstractNode);
		
		if(flowNode instanceof Activity){
			
		}
		return node;
	}
	
	
	
	private EClass createTheAbstractNode(){
		EClass node = ecoreFactory.createEClass();
		node.setName("BPNode");
		node.setAbstract(true);
		
		return node;
	}
	
	
	public EPackage getGlueModel(){	
		return this.gluePackage;
	}
	
	public GlueModelNode getNodeByName(String name){
		return this.nodesByName.get(name);
	}
	
	public GlueModelNode getNodeByFlowNode(FlowNode flowNode){
		return this.glueNodeFlowNodeMap.inverse().get(flowNode);
	}
	
	public Collection<GlueModelNode> getNodes(){
		return this.nodesByName.values();
	}
	
	public void save() throws IOException{
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			    "ecore", new  XMLResourceFactoryImpl());
		Resource resource = 
				resourceSet.createResource(URI.createFileURI(new File("models/amazon_sd/gluemodel.ecore").getAbsolutePath()));
		resource.getContents().add(gluePackage);
		resource.save(null);
	}
	
}
