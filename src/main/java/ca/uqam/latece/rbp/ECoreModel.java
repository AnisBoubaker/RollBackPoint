package ca.uqam.latece.rbp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;


//import cbpmn.CbpmnPackage;

public class ECoreModel {
	
	private EPackage modelPackage;
	private Resource resource;
	private final String defaultFolder = "models/";
	
	
	public ECoreModel(String modelResourcePath){
		load(modelResourcePath);
	}
	
	private EPackage load(String filePath){
		// Initialize the model
		EcorePackage.eINSTANCE.eClass();
		
		// Register the XMI resource factory for the .rea extension
	    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = reg.getExtensionToFactoryMap();
	    m.put("ecore", new EcoreResourceFactoryImpl());
	    

	    // Obtain a new resource set
	    ResourceSet resSet = new ResourceSetImpl();
	    
	    //ProjectMap.getAdapter(resSet);

	    // Get the resource
	    this.resource = resSet.getResource(URI.createFileURI(new File(this.defaultFolder+filePath).getAbsolutePath()), true);
	    // Get the first model element and cast it to the right type, in my
	    // example everything is hierarchical included in this first node
	    this.modelPackage = (EPackage) resource.getContents().get(0);
	    return this.modelPackage;
	}
	
	public EPackage getEPackage(){
		return this.modelPackage;
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
