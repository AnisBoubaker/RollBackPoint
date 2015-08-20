package ca.uqam.latece.rbp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.ecore.Variable;
import org.eclipse.ocl.ecore.utilities.AbstractVisitor;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.VariableExp;


public class RbpVisitor extends AbstractVisitor<Object> {

	private Set<String> referredFeatures;
	private Set<String> currentIteratorSources;
	private Map<String, String> globalVariables;
	private boolean readingIteratorSource = false;
	private boolean isInCompoundPropertyCall = false;
	
	protected RbpVisitor(){
		this(new Object());
	}
	
	private RbpVisitor(Object result){
		super(result);
		this.referredFeatures = new HashSet<String>();
		this.globalVariables = new HashMap<String, String>();
	}
	
	public Set<String> getReferredFeatures(){
		return this.referredFeatures;
	}

	

	@Override
	protected Object handleIteratorExp(
			IteratorExp<EClassifier, EParameter> callExp, Object sourceResult,
			List<Object> variableResults, Object bodyResult) {
		
		
		return super.handleIteratorExp(callExp, sourceResult, variableResults,
				bodyResult);
	}
	
	
	@Override
	public Object visitIteratorExp(IteratorExp<EClassifier, EParameter> callExp) {
		
		Set<String> saveIteratorSources = this.currentIteratorSources;
		this.currentIteratorSources = new HashSet<String>();
		this.readingIteratorSource = true;
		Object sourceResult = safeVisit(callExp.getSource());
		this.readingIteratorSource = false;
		
		String source; 
		if(sourceResult instanceof List<?>){
			source = ((List<?>) sourceResult).get(0).toString();
		} else {
			source = sourceResult.toString();
		}
        
        List<Object> variableResults;
        List<org.eclipse.ocl.expressions.Variable<EClassifier, EParameter>> variables = callExp.getIterator();
        
        List<String> retainedVariables = new ArrayList<String>();
        
        if (variables.isEmpty()) {
            variableResults = Collections.emptyList();
        } else {
            variableResults = new java.util.ArrayList<Object>(variables.size());
            for (org.eclipse.ocl.expressions.Variable<EClassifier, EParameter> iterVar : variables) {
                variableResults.add(safeVisit((Variable)iterVar));
                this.globalVariables.put(iterVar.getName(), source);
                retainedVariables.add(iterVar.getName());
            }
        }
        
        Object bodyResult = safeVisit(callExp.getBody());
        
        for(String retained : retainedVariables){
        	this.globalVariables.remove(retained);
        }
        
        //Features found in the source of the iterator should be added to the 
        //referredFeatures only if we did not use the feature in the body of
        //the iterator. 
        for(String sourceStr : this.currentIteratorSources){
        	boolean found = false;
        	for(String feature : this.referredFeatures){
        		if(feature.contains(sourceStr)){
        			found = true;
        			break;
        		}
        	}
        	if(!found) this.referredFeatures.add(sourceStr);
        }
        
        this.currentIteratorSources = saveIteratorSources;
        return handleIteratorExp(callExp, sourceResult, variableResults, bodyResult);
	}

	@Override
	protected Object handleCollectionItem(CollectionItem<EClassifier> item,
			Object itemResult) {
		
		return itemResult;
	}

	@Override
	protected Object handleCollectionLiteralExp(
			CollectionLiteralExp<EClassifier> literalExp,
			List<Object> partResults) {
		return partResults;
	}
	
	@Override
	protected Object handlePropertyCallExp(
			org.eclipse.ocl.expressions.PropertyCallExp<EClassifier, EStructuralFeature> callExp,
			Object sourceResult, List<Object> qualifierResults) {
		
			String result = callExp.getReferredProperty().getName();
			if(sourceResult!="") result = sourceResult+"."+result;
		return result;
	}
	
	
	@Override
	public Object visitPropertyCallExp(
			PropertyCallExp<EClassifier, EStructuralFeature> callExp) {
		// source is null when the property call expression is an
        //    association class navigation qualifier
		boolean iStartedTheCompoundPropertyCall = false;
		if(!this.isInCompoundPropertyCall){
			this.isInCompoundPropertyCall = true;
			iStartedTheCompoundPropertyCall = true;
		}
		
        Object sourceResult = safeVisit(callExp.getSource());
        
        List<Object> qualifierResults;
        List<OCLExpression<EClassifier>> qualifiers = callExp.getQualifier();
        
        if (qualifiers.isEmpty()) {
            qualifierResults = Collections.emptyList();
        } else {
            qualifierResults = new java.util.ArrayList<Object>(qualifiers.size());
            for (OCLExpression<EClassifier> qual : qualifiers) {
                qualifierResults.add(safeVisit(qual));
            }
        }
        
        if(iStartedTheCompoundPropertyCall){
        	this.isInCompoundPropertyCall = false;
        	String sourceStr=null;
        	if(sourceResult instanceof String) sourceStr = (String)sourceResult;
        	else sourceStr = "";
    		if(sourceStr.compareTo("")!=0) sourceStr+=".";
    		String result = sourceStr+callExp.getReferredProperty().getName();
    		if(readingIteratorSource) 
    			this.currentIteratorSources.add(result);
    		else 
    			this.referredFeatures.add(result);
        }
        
        return handlePropertyCallExp(callExp, sourceResult, qualifierResults);
	}
	
	@Override
	public Object visitVariableExp(VariableExp<EClassifier, EParameter> v) {
		if(v.getName().compareTo("self")==0) 
			return "";
		
		Variable referredVar = (Variable)(v.getReferredVariable());
		if(this.globalVariables.containsKey(v.getName())) 
			return this.globalVariables.get(v.getName());
		
		return referredVar.getName();
	}

}
