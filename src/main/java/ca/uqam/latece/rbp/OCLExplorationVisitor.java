package ca.uqam.latece.rbp;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.ecore.utilities.AbstractVisitor;
import org.eclipse.ocl.expressions.AssociationClassCallExp;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.CollectionRange;
import org.eclipse.ocl.expressions.EnumLiteralExp;
import org.eclipse.ocl.expressions.IfExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.InvalidLiteralExp;
import org.eclipse.ocl.expressions.IterateExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.LetExp;
import org.eclipse.ocl.expressions.MessageExp;
import org.eclipse.ocl.expressions.NullLiteralExp;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StateExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.expressions.UnspecifiedValueExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.Visitable;

public class OCLExplorationVisitor extends AbstractVisitor<String> {

	private boolean showVisits = false;
	private boolean showHandles = true;

	@Override
	protected String safeVisit(Visitable v) {
		// TODO Auto-generated method stub
		return super.safeVisit(v);
	}

	@Override
	public String visitOperationCallExp(
			OperationCallExp<EClassifier, EOperation> callExp) {
		if (showVisits)
			System.out.println("Visiting OperationCallExp: " + callExp);
		return super.visitOperationCallExp(callExp);
	}

	@Override
	protected String handleOperationCallExp(
			OperationCallExp<EClassifier, EOperation> callExp,
			String sourceResult, List<String> argumentResults) {
		if (showHandles) {
			System.out
					.println("---------------- IN: OperationCall ----------------");
			System.out.println("callExp=" + callExp);
			System.out.println("sourceResult=" + sourceResult);
			System.out.println("argumentsResult=" + argumentResults);
		}
		return super.handleOperationCallExp(callExp, sourceResult,
				argumentResults);
	}

	@Override
	public String visitVariableExp(VariableExp<EClassifier, EParameter> v) {
		if (showVisits)
			System.out.println("Visiting VariableExp: " + v + " with referenced variable: "+ v.getReferredVariable());
		return super.visitVariableExp(v);
	}

	@Override
	public String visitPropertyCallExp(
			PropertyCallExp<EClassifier, EStructuralFeature> callExp) {
		if (showVisits)
			System.out.println("Visiting PropertyCallExp: " + callExp + " with type: "+ ((EClassifier)callExp.getType()).getName());
		return super.visitPropertyCallExp(callExp);
	}

	@Override
	protected String handlePropertyCallExp(
			PropertyCallExp<EClassifier, EStructuralFeature> callExp,
			String sourceResult, List<String> qualifierResults) {
		if (showHandles) {
			System.out
					.println("---------------- IN: PropertyCallExp ----------------");
			System.out.println("callExp=" + callExp);
			System.out.println("sourceResult=" + sourceResult);
			System.out.println("argumentsResult=" + qualifierResults);
		}
		return super.handlePropertyCallExp(callExp, sourceResult,
				qualifierResults);
	}

	@Override
	public String visitAssociationClassCallExp(
			AssociationClassCallExp<EClassifier, EStructuralFeature> callExp) {

		if (showVisits)
			System.out.println("Visiting AssociationCallExp: " + callExp);
		return super.visitAssociationClassCallExp(callExp);
	}

	@Override
	protected String handleAssociationClassCallExp(
			AssociationClassCallExp<EClassifier, EStructuralFeature> callExp,
			String sourceResult, List<String> qualifierResults) {
		if (showHandles) {
			System.out
					.println("---------------- IN: AssociationClassCallExp ----------------");
			System.out.println("[callExp] " + callExp);
			System.out.println("[sourceResult] " + sourceResult);
			System.out.println("[qualifierResults] " + qualifierResults);
		}
		return super.handleAssociationClassCallExp(callExp, sourceResult,
				qualifierResults);
	}

	@Override
	public String visitVariable(Variable<EClassifier, EParameter> variable) {
		if (showVisits){
			System.out.println("Visiting Variable: " + variable.getName() + " with type: "+ ((EClassifier)variable.getType()).getName() + " with represented parameter: "+variable.getRepresentedParameter());
		}
		return super.visitVariable(variable);
	}

	@Override
	protected String handleVariable(Variable<EClassifier, EParameter> variable,
			String initResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: Variable ----------------");
			System.out.println("[part] " + variable);
			System.out.println("[valueResult] " + initResult);
		}
		result+="Variable: "+variable+"\n";
		return super.handleVariable(variable, initResult);
	}

	@Override
	public String visitIfExp(IfExp<EClassifier> ifExp) {
		if (showVisits)
			System.out.println("Visiting IfExp: " + ifExp);
		return super.visitIfExp(ifExp);
	}

	@Override
	protected String handleIfExp(IfExp<EClassifier> ifExp,
			String conditionResult, String thenResult, String elseResult) {
		if (showHandles) {
			System.out.println("---------------- IN: IfExp ----------------");
			System.out.println("[ifExp] " + ifExp);
			System.out.println("[conditionResult] " + conditionResult);
			System.out.println("[thenResult] " + thenResult);
			System.out.println("[elseResult] " + elseResult);
		}
		return super
				.handleIfExp(ifExp, conditionResult, thenResult, elseResult);
	}

	@Override
	public String visitTypeExp(TypeExp<EClassifier> t) {
		if (showVisits)
			System.out.println("Visiting TypeExp: " + t);
		return super.visitTypeExp(t);
	}

	@Override
	public String visitMessageExp(
			MessageExp<EClassifier, CallOperationAction, SendSignalAction> messageExp) {
		if (showVisits)
			System.out.println("Visiting MessageExp: " + messageExp);
		return super.visitMessageExp(messageExp);
	}

	@Override
	protected String handleMessageExp(
			MessageExp<EClassifier, CallOperationAction, SendSignalAction> messageExp,
			String targetResult, List<String> argumentResults) {
		if (showHandles) {
			System.out
					.println("---------------- IN: MessageExp ----------------");
			System.out.println("[messageExp] " + messageExp);
			System.out.println("[targetResult] " + targetResult);
			System.out.println("[argumentResults] " + argumentResults);
		}
		return super
				.handleMessageExp(messageExp, targetResult, argumentResults);
	}

	@Override
	public String visitUnspecifiedValueExp(
			UnspecifiedValueExp<EClassifier> unspecExp) {
		if (showVisits)
			System.out.println("Visiting UnspecifiedValueExp: " + unspecExp);
		return super.visitUnspecifiedValueExp(unspecExp);
	}

	@Override
	public String visitStateExp(StateExp<EClassifier, EObject> stateExp) {
		if (showVisits)
			System.out.println("Visiting StateExp: " + stateExp);
		return super.visitStateExp(stateExp);
	}

	@Override
	public String visitIntegerLiteralExp(
			IntegerLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting IntegerLiteralExp: " + literalExp);
		return super.visitIntegerLiteralExp(literalExp);
	}

	@Override
	public String visitUnlimitedNaturalLiteralExp(
			UnlimitedNaturalLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting UnlimitedNaturalLiteralExp: "
					+ literalExp);
		return super.visitUnlimitedNaturalLiteralExp(literalExp);
	}

	@Override
	public String visitRealLiteralExp(RealLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting RealLiteralExp: " + literalExp);
		return super.visitRealLiteralExp(literalExp);
	}

	@Override
	public String visitStringLiteralExp(StringLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting StringLiteralExp: " + literalExp);
		return super.visitStringLiteralExp(literalExp);
	}

	@Override
	public String visitBooleanLiteralExp(
			BooleanLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting BooleanLiteralExp: " + literalExp);
		return super.visitBooleanLiteralExp(literalExp);
	}

	@Override
	public String visitNullLiteralExp(NullLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting NullLiteralExp: " + literalExp);
		return super.visitNullLiteralExp(literalExp);
	}

	@Override
	public String visitInvalidLiteralExp(
			InvalidLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting InvalidLiteralExp: " + literalExp);
		return super.visitInvalidLiteralExp(literalExp);
	}

	@Override
	public String visitTupleLiteralExp(
			TupleLiteralExp<EClassifier, EStructuralFeature> literalExp) {
		if (showVisits)
			System.out.println("Visiting TupleLiteralExp: " + literalExp);
		return super.visitTupleLiteralExp(literalExp);
	}

	@Override
	protected String handleTupleLiteralExp(
			TupleLiteralExp<EClassifier, EStructuralFeature> literalExp,
			List<String> partResults) {
		if (showHandles) {
			System.out
					.println("---------------- IN: TupleLiteralExp ----------------");
			System.out.println("[literalExp] " + literalExp);
			System.out.println("[partResults] " + partResults);
		}
		return super.handleTupleLiteralExp(literalExp, partResults);
	}

	@Override
	public String visitTupleLiteralPart(
			TupleLiteralPart<EClassifier, EStructuralFeature> part) {
		if (showVisits)
			System.out.println("Visiting TupleLiteralPart: " + part);
		return super.visitTupleLiteralPart(part);
	}

	@Override
	protected String handleTupleLiteralPart(
			TupleLiteralPart<EClassifier, EStructuralFeature> part,
			String valueResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: TupleLiteralPart ----------------");
			System.out.println("[part] " + part);
			System.out.println("[valueResult] " + valueResult);
		}
		return super.handleTupleLiteralPart(part, valueResult);
	}

	@Override
	public String visitLetExp(LetExp<EClassifier, EParameter> letExp) {
		if (showVisits)
			System.out.println("Visiting LetExp: " + letExp);
		return super.visitLetExp(letExp);
	}

	@Override
	protected String handleLetExp(LetExp<EClassifier, EParameter> letExp,
			String variableResult, String inResult) {
		if (showHandles) {
			System.out.println("---------------- IN: LetExp ----------------");
			System.out.println("[letExp] " + letExp);
			System.out.println("[variableResult] " + variableResult);
			System.out.println("[inResult] " + inResult);
		}
		return super.handleLetExp(letExp, variableResult, inResult);
	}

	@Override
	public String visitEnumLiteralExp(
			EnumLiteralExp<EClassifier, EEnumLiteral> literalExp) {
		if (showVisits)
			System.out.println("Visiting EnulLiteralExp: " + literalExp);
		return super.visitEnumLiteralExp(literalExp);
	}

	@Override
	public String visitCollectionLiteralExp(
			CollectionLiteralExp<EClassifier> literalExp) {
		if (showVisits)
			System.out.println("Visiting CollectionLiteralExp: " + literalExp);
		return super.visitCollectionLiteralExp(literalExp);
	}

	@Override
	protected String handleCollectionLiteralExp(
			CollectionLiteralExp<EClassifier> literalExp,
			List<String> partResults) {
		if (showHandles) {
			System.out
					.println("---------------- IN: CollectionLiteralExp ----------------");
			System.out.println("[literalExp] " + literalExp);
			System.out.println("[partResults] " + partResults);
		}
		return super.handleCollectionLiteralExp(literalExp, partResults);
	}

	@Override
	public String visitCollectionItem(CollectionItem<EClassifier> item) {
		if (showVisits)
			System.out.println("Visiting CollectionItem: " + item + " with type : "+((EClassifier)item.getType()).getName());
		return super.visitCollectionItem(item);
	}

	@Override
	protected String handleCollectionItem(CollectionItem<EClassifier> item,
			String itemResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: CollectionItem ----------------");
			System.out.println("[item] " + item);
			System.out.println("[itemResult] " + itemResult);
		}
		result+="CollectionItem: "+item+" [itemResult]"+itemResult+"\n";
		return super.handleCollectionItem(item, itemResult);
	}

	@Override
	public String visitCollectionRange(CollectionRange<EClassifier> range) {
		if (showVisits)
			System.out.println("Visiting CollectionRange: " + range);
		result+="CollectionRange: "+range+"\n";
		return super.visitCollectionRange(range);
	}

	@Override
	protected String handleCollectionRange(CollectionRange<EClassifier> range,
			String firstResult, String lastResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: CollectionRange ----------------");
			System.out.println("[range] " + range);
			System.out.println("[firstResult] " + firstResult);
			System.out.println("[lastResult] " + lastResult);
		}
		return super.handleCollectionRange(range, firstResult, lastResult);
	}

	@Override
	public String visitIteratorExp(IteratorExp<EClassifier, EParameter> callExp) {
		if (showVisits)
			System.out.println("Visiting IteratorExp["+callExp.getName()+"]: " + callExp+ " "+callExp.getName());
		return super.visitIteratorExp(callExp);
	}

	@Override
	protected String handleIteratorExp(
			IteratorExp<EClassifier, EParameter> callExp, String sourceResult,
			List<String> variableResults, String bodyResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: IteratorExp ----------------");
			System.out.println("[callExp] " + callExp);
			System.out.println("[sourceResult] " + sourceResult);
			System.out.println("[variableResult] " + variableResults);
			System.out.println("[bodyResults] " + bodyResult);
		}
		return super.handleIteratorExp(callExp, sourceResult, variableResults,
				bodyResult);
	}

	@Override
	public String visitIterateExp(IterateExp<EClassifier, EParameter> callExp) {
		if (showVisits)
			System.out.println("Visiting IterateExp: " + callExp);
		return super.visitIterateExp(callExp);
	}

	@Override
	protected String handleIterateExp(
			IterateExp<EClassifier, EParameter> callExp, String sourceResult,
			List<String> variableResults, String resultResult, String bodyResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: IterateExp ----------------");
			System.out.println("[callExp] " + callExp);
			System.out.println("[sourceResult] " + sourceResult);
			System.out.println("[variableResult] " + variableResults);
			System.out.println("[resultResult] " + resultResult);
			System.out.println("[bodyResults] " + bodyResult);
		}
		return super.handleIterateExp(callExp, sourceResult, variableResults,
				resultResult, bodyResult);
	}

	@Override
	public String visitExpressionInOCL(
			ExpressionInOCL<EClassifier, EParameter> expression) {
		if (showVisits)
			System.out.println("Visiting ExpressionInOCL: " + expression);
		return super.visitExpressionInOCL(expression);
	}

	@Override
	protected String handleExpressionInOCL(
			ExpressionInOCL<EClassifier, EParameter> expression,
			String contextResult, String resultResult,
			List<String> parameterResults, String bodyResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: ExpressionInOCL ----------------");
			System.out.println("[expression] " + expression);
			System.out.println("[resultResult] " + resultResult);
			System.out.println("[parameterResults] " + parameterResults);
			System.out.println("[bodyResult] " + bodyResult);
		}
		return super.handleExpressionInOCL(expression, contextResult,
				resultResult, parameterResults, bodyResult);
	}

	@Override
	public String visitConstraint(Constraint constraint) {
		if (showVisits)
			System.out.println("Visiting Constraint: " + constraint);
		return super.visitConstraint(constraint);
	}

	@Override
	protected String handleConstraint(Constraint constraint,
			String specificationResult) {
		if (showHandles) {
			System.out
					.println("---------------- IN: Constraint ----------------");
			System.out.println("[constraint] " + constraint);
			System.out.println("[specificationResult] " + specificationResult);
		}
		return super.handleConstraint(constraint, specificationResult);
	}

	@Override
	protected ExpressionInOCL<EClassifier, EParameter> getSpecification(
			Constraint constraint) {

		return constraint.getSpecification();
	}

}
