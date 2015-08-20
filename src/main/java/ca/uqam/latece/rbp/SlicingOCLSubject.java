package ca.uqam.latece.rbp;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.ExpressionInOCL;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.OCLExpression;

import cbpmn.OCLConstraint;

public class SlicingOCLSubject {

	private String name;
	private OCLExpression oclExpression;
	private OCLConstraint oclSource;
	private EClass context;
	
	private Set<String> referredVariables;
	
	/**
	 * @return the variables that the OCL expression refers to.
	 */
	public Set<String> getReferredVariables() {
		return referredVariables;
	}
	
	public SlicingOCLSubject(OCLConstraint oclConstraint, EClass context) throws ParserException{
		this(oclConstraint, context, false);
	}

	public SlicingOCLSubject(OCLConstraint oclConstraint, EClass context, boolean isQuery) throws ParserException{
		this.name = oclConstraint.getConstraintName();
		this.oclSource = oclConstraint;
		this.context = context;
		
		if(isQuery){
			this.oclExpression = this.createOCLExpressionForQuery(this.oclSource, this.context);
		} else {
			this.oclExpression = this.createOCLExpressionForConstraint(this.oclSource, this.context);
		}
		this.computeReferredVariables();
	}
	
	/**
	 * @return the oclExpression
	 */
	public OCLExpression getOclExpression() {
		return oclExpression;
	}

	private OCLExpression createOCLExpressionForConstraint(OCLConstraint constraint, EClass node) throws ParserException{
		OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
		OCL.Helper helper = ocl.createOCLHelper();
		helper.setContext(node);
		try{
			Constraint invariant = helper.createInvariant(constraint.getConstraintStr());
			invariant.setName(constraint.getConstraintName());
			ExpressionInOCL spec = (ExpressionInOCL) invariant.getSpecification();
			OCLExpression oclExpression = (OCLExpression) spec.getBodyExpression();
			return oclExpression;
		} catch (org.eclipse.ocl.SemanticException e){
			System.err.println("Semantic exception occured: "+e.getMessage());
			System.err.println("For OCL constraint: ["+node.getName()+"::"+constraint.getConstraintName()+"]"+constraint.getConstraintStr());
			//e.printStackTrace();
			return null;
		}		
	}
	
	private OCLExpression createOCLExpressionForQuery(OCLConstraint constraint, EClass node) throws ParserException{
		OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
		OCL.Helper helper = ocl.createOCLHelper();
		helper.setContext(node);
		OCLExpression oclExpression = helper.createQuery(constraint.getConstraintStr());
		return oclExpression;
	}
	
	private void computeReferredVariables(){
		RbpVisitor visitor = new RbpVisitor();
		if(this.oclExpression!=null){
			this.oclExpression.accept(visitor);
			this.referredVariables = visitor.getReferredFeatures();
		}
	}
	
}
