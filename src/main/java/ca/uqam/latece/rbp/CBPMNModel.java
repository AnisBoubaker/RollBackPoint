package ca.uqam.latece.rbp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import cbpmn.*;

public class CBPMNModel {

	private ProcessModel processModel;
	private Resource resource;
	private final String defaultFolder = "models/";
	
	public CBPMNModel(String modelResourcePath){
		load(modelResourcePath);
	}
	
	private ProcessModel load(String filePath){
		// Initialize the model
		CbpmnPackage.eINSTANCE.eClass();
		
		// Register the XMI resource factory for the .rea extension
	    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = reg.getExtensionToFactoryMap();
	    m.put("cbpmn", new XMIResourceFactoryImpl());

	    // Obtain a new resource set
	    ResourceSet resSet = new ResourceSetImpl();

	    // Get the resource
	    this.resource = resSet.getResource(URI
	        .createFileURI(new File(this.defaultFolder+filePath).getAbsolutePath()), true);
	    // Get the first model element and cast it to the right type, in my
	    // example everything is hierarchical included in this first node
	    this.processModel = (ProcessModel) resource.getContents().get(0);
	    return this.processModel;
	}
	
	public ProcessModel getProcessModel(){
		return this.processModel;
	}
	
	public void save(){
		try {
	      resource.save(Collections.EMPTY_MAP);
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	}
	
}
