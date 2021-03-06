package husacct.analyse.task.analyser.csharp.generators;

import husacct.analyse.infrastructure.antlr.csharp.CSharpParser;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import static husacct.analyse.task.analyser.csharp.generators.CSharpGeneratorToolkit.*;

public class CSharpBlockScopeGenerator extends CSharpGenerator {

	private String packageAndClassName;
	private String belongsToMethod;

	public void walkThroughBlockScope(CommonTree tree, String packageAndClassName, String belongsToMethod) {
		this.packageAndClassName = packageAndClassName;
		this.belongsToMethod = belongsToMethod;

		walkThroughBlockScope(tree);		
	}

	private void walkThroughBlockScope(Tree tree) {
		for (int i = 0; i < tree.getChildCount(); i++) {
			Tree child = tree.getChild(i);
			switch (child.getType()) {
				case CSharpParser.VARIABLE_DECLARATOR:
					delegateLocalVariable(child);
					deleteTreeChild(child);
					break;
				case CSharpParser.LOCAL_VARIABLE_DECLARATOR:
					delegateLocalVariable(child);
					deleteTreeChild(child);
					break;
				case CSharpParser.OBJECT_CREATION_EXPRESSION:
					delegateInvocationConstructor(child);
					break;
				case CSharpParser.METHOD_INVOCATION:
					delegateInvocationMethod(child);
					break;
				case CSharpParser.MEMBER_ACCESS:
					delegateInvocationPropertyOrField(child);
					break;
				case CSharpParser.THROW:
				case CSharpParser.CATCH:
					delegateException(child);
					break;
				case CSharpParser.FOREACH:
				case CSharpParser.FOR:
				case CSharpParser.WHILE:
				case CSharpParser.DO:
					delegateLoop(child);
					break;
				default:
					walkThroughBlockScope(child);
			}
		}
	}

	private void delegateLocalVariable(Tree tree) {
		if (tree.toStringTree().contains("= >")) {
			CSharpLamdaGenerator csLamdaGenerator = new CSharpLamdaGenerator();
			csLamdaGenerator.delegateLambdaToBuffer((CommonTree)tree, packageAndClassName, belongsToMethod);
		} else {
			CSharpAttributeAndLocalVariableGenerator csharpLocalVariableGenerator = new CSharpAttributeAndLocalVariableGenerator();
			csharpLocalVariableGenerator.generateLocalVariableToDomain(tree, this.packageAndClassName, this.belongsToMethod);
		}
	}

	private void delegateInvocationConstructor(Tree tree) {
		CSharpInvocationConstructorGenerator csharpInvocationConstructorGenerator= new CSharpInvocationConstructorGenerator(this.packageAndClassName);
		csharpInvocationConstructorGenerator.generateConstructorInvocToDomain((CommonTree) tree, this.belongsToMethod);
	}

	private void delegateInvocationMethod(Tree tree) {
		CSharpInvocationMethodGenerator csharpInvocationMethodGenerator = new CSharpInvocationMethodGenerator(this.packageAndClassName);
		csharpInvocationMethodGenerator.generateMethodInvocToDomain((CommonTree) tree, this.belongsToMethod);
	}

	private void delegateInvocationPropertyOrField(Tree tree) {
		CSharpInvocationPropertyOrFieldGenerator csharpInvocationPropertyOrFieldGenerator = new CSharpInvocationPropertyOrFieldGenerator(this.packageAndClassName);
		csharpInvocationPropertyOrFieldGenerator.generatePropertyOrFieldInvocToDomain((CommonTree) tree, this.belongsToMethod);
	}

	private void delegateException(Tree tree) {
		CSharpExceptionGenerator csharpExceptionGenerator = new CSharpExceptionGenerator();
		csharpExceptionGenerator.generateExceptionToDomain((CommonTree) tree, this.packageAndClassName);
	}

	private void delegateLoop(Tree tree) {
		CSharpLoopGenerator csharpLoopGenerator = new CSharpLoopGenerator();
		csharpLoopGenerator.generateToDomainFromLoop((CommonTree) tree, this.packageAndClassName, this.belongsToMethod);
	}
}
