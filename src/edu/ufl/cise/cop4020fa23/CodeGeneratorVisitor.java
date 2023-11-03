package edu.ufl.cise.cop4020fa23;

import java.util.Map;


import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;

public class CodeGeneratorVisitor implements ASTVisitor {


//    @Override
//    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//
//        // Get the name of the program
//        String className = program.getName();
//
//        // Get the return type of the program
//        Type returnType = program.getType();
//
//        // Convert PLC types to Java types
//        String javaReturnType = convertTypeToJava(returnType);
//
//        // Adding package declaration
//        String packageName = "edu.ufl.cise.cop4020fa23";
//        code.append(String.format("package %s;\n\n", packageName));
//
//        // Visit the parameters (name definitions)
//        StringBuilder params = new StringBuilder();
//        for (NameDef param : program.getParams()) {
//            // Assuming that the visit method for NameDef returns a string of the form "type name"
//            String paramCode = (String) param.visit(this, arg);
//            if (params.length() > 0) params.append(", ");
//            params.append(paramCode);
//        }
//
//        // Visit the block (body of the apply method)
//        String blockCode = (String) program.getBlock().visit(this, arg);
//
//        // Format the generated code
//        code.append(String.format("public class %s {\n", className));
//        code.append(String.format("    public static %s apply(%s) {\n", javaReturnType, params));
//        code.append(blockCode);
//        code.append("    }\n");
//        code.append("}\n");
//
//        return code.toString();
//    }
//
//    private String convertTypeToJava(Type type) {
//        switch (type) {
//            case VOID:
//                return "void";
//            case INT:
//                return "int";
//            // Add other type conversions as needed
//            default:
//                return type.toString().toLowerCase();
//        }
//    }


    @Override
    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();

        // Get the name of the program
        String className = program.getName();

        // Get the return type of the program
        Type returnType = program.getType();
        String javaReturnType = getJavaType(returnType);  // Convert to Java type

        // Adding package declaration
        String packageName = "edu.ufl.cise.cop4020fa23";
        code.append(String.format("package %s;\n\n", packageName));

        // Visit the parameters (name definitions)
        StringBuilder params = new StringBuilder();
        for (NameDef param : program.getParams()) {
            String paramName = "param_" + param.getName();
            String paramType = getJavaType(param.getType());  // Convert to Java type
            String paramCode = String.format("%s %s", paramType, paramName);
            if (params.length() > 0) params.append(", ");
            params.append(paramCode);
        }

        // Set the modified parameter names in the argument (if it's a Map)
        if (arg instanceof Map) {
            Map<String, String> argMap = (Map<String, String>) arg;
            for (NameDef param : program.getParams()) {
                argMap.put(param.getName(), "param_" + param.getName());
            }
        }

        // Visit the block (body of the apply method)
        String blockCode = (String) program.getBlock().visit(this, arg);

        // Format the generated code
        code.append(String.format("public class %s {\n", className));
        code.append(String.format("    public static %s apply(%s) {\n", javaReturnType, params));
        code.append(blockCode);
        code.append("    }\n");
        code.append("}\n");

        return code.toString();
    }


    private String getJavaType(Type type) {
        switch (type) {
            case BOOLEAN: return "boolean";
            case INT: return "int";
            // Add other type conversions as needed
            default: return type.toString().toLowerCase();
        }
    }



    @Override
    public Object visitBlock(Block block, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();

        // Start the block
        code.append("{\n");

        // Visit each block element and append the generated code
        for (Block.BlockElem blockElem : block.getElems()) {
            String blockElemCode = (String) blockElem.visit(this, arg);
            code.append(blockElemCode);
            // Add a semicolon after each declaration or statement
//            if (blockElem instanceof Declaration || blockElem instanceof Statement) {
//                code.append(";\n");
//            }
        }

        // End the block
        code.append("}\n");

        return code.toString();
    }


    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();

        // Map from programming language types to Java types
        Map<Type, String> typeMapping = Map.of(
                Type.INT, "int",
                Type.BOOLEAN, "boolean",
                Type.STRING, "String",
                Type.VOID, "void",
                Type.IMAGE, "ImageType", // Replace "ImageType" with the actual Java type for IMAGE
                Type.PIXEL, "PixelType"  // Replace "PixelType" with the actual Java type for PIXEL
        );

        // Get the programming language type
        Type type = nameDef.getType();

        // Get the corresponding Java type and append it to the code
        String javaType = typeMapping.get(type);
        if (javaType == null) {
            throw new PLCCompilerException("Unsupported type: " + type);
        }
        code.append(javaType).append(" ");

        // Append the Java name of the identifier
        String javaName = nameDef.getName(); // Assuming the Java name is the same as the identifier's text
        code.append(javaName);

        // Handle the optional dimension
        Dimension dimension = nameDef.getDimension();
        if (dimension != null) {
            String dimensionCode = (String) dimension.visit(this, arg);
            code.append(dimensionCode);
        }

        return code.toString();
    }


//    @Override
//    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCCompilerException {
//        // Get the NameDef node from the Declaration
//        NameDef nameDef = declaration.getNameDef();
//
//        // Visit the NameDef node to generate the Java code for it
//        String nameDefCode = (String) nameDef.visit(this, arg);
//
//        // Return the generated code
//        return nameDefCode;
//    }


    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();

        // Visit the NameDef node to generate the Java code for the declaration part
        NameDef nameDef = declaration.getNameDef();
        String nameDefCode = (String) nameDef.visit(this, arg);
        code.append(nameDefCode);

        // Check if there is an initializer
        Expr initializer = declaration.getInitializer();
        if (initializer != null) {
            // Visit the Expr node to generate the Java code for the expression part
            String exprCode = (String) initializer.visit(this, arg);

            // Combine the generated code for the declaration and the expression
            code.append(" = ").append(exprCode);
        }

        // Add a semicolon at the end of the declaration
        code.append(";");

        // Return the generated code
        return code.toString();
    }


    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCCompilerException {
        return stringLitExpr.getText();
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCCompilerException {
        return numLitExpr.getText();
    }

//    @Override
//    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
//        if (identExpr.getNameDef() != null) {
//            return identExpr.getNameDef().getJavaName();
//        } else {
//            throw new PLCCompilerException("Identifier not defined: " + identExpr.getName());
//        }
//    }


//    @Override
//    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
//        // Retrieve the modified name from the argument (if it's a Map)
//        String name = identExpr.getName();
//        if (arg instanceof Map) {
//            Map<String, String> argMap = (Map<String, String>) arg;
//            name = argMap.getOrDefault(name, name);
//        }
//        return name;
//    }


    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
        // Assuming that 'arg' is a map that contains the mapping from identifier names to their corresponding parameter names
        Map<String, String> paramMap = (Map<String, String>) arg;

        // Get the original name of the identifier
        String originalName = identExpr.getName();

        // Check if there is a mapping for this identifier name
        String paramName = paramMap.getOrDefault(originalName, originalName);

        // Return the parameter name
        return paramName;
    }




    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws PLCCompilerException {
        return Boolean.parseBoolean(booleanLitExpr.getText()) ? "true" : "false";
    }


    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();

        // Generate code for the guard expression
        Object guardExprCode = conditionalExpr.getGuardExpr().visit(this, arg);
        sb.append("(").append(guardExprCode).append(" ? ");

        // Generate code for the true expression
        Object trueExprCode = conditionalExpr.getTrueExpr().visit(this, arg);
        sb.append(trueExprCode).append(" : ");

        // Generate code for the false expression
        Object falseExprCode = conditionalExpr.getFalseExpr().visit(this, arg);
        sb.append(falseExprCode).append(")");

        return sb.toString();
    }


    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();

        // Retrieve the left expression's code and type
        Object leftExprCode = binaryExpr.getLeftExpr().visit(this, arg);
        Type leftExprType = binaryExpr.getLeftExpr().getType();

        // Retrieve the right expression's code
        Object rightExprCode = binaryExpr.getRightExpr().visit(this, arg);

        // Get the operator kind
        Kind opKind = binaryExpr.getOpKind();

        // Check for string equality
        if (leftExprType == Type.STRING && opKind == Kind.EQ) {
            sb.append(leftExprCode).append(".equals(").append(rightExprCode).append(")");
        }
        // Check for exponentiation
        else if (opKind == Kind.EXP) {
            sb.append("((int)Math.round(Math.pow(").append(leftExprCode).append(", ").append(rightExprCode).append(")))");
        }
        // Handle other binary operators
        else {
            String operator = switch(opKind) {
                case PLUS -> "+";
                case MINUS -> "-";
                case TIMES -> "*";
                case DIV -> "/";
                case MOD -> "%";
                case AND -> "&&";
                case OR -> "||";
                case EQ -> "==";
                case BANG -> "!";
                case LT -> "<";
                case GT -> ">";
                case LE -> "<=";
                case GE -> ">=";
                default -> throw new PLCCompilerException("Unsupported binary operator: " + opKind);
            };
            sb.append("(").append(leftExprCode).append(" ").append(operator).append(" ").append(rightExprCode).append(")");
        }

        return sb.toString();
    }


    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();

        // Retrieve the expression's code
        Object exprCode = unaryExpr.getExpr().visit(this, arg);

        // Get the operator kind
        Kind opKind = unaryExpr.getOp();

        // Handle unary operators
        String operator = switch(opKind) {
            case PLUS -> "+";
            case MINUS -> "-";
            case BANG -> "!";
            default -> throw new PLCCompilerException("Unsupported unary operator: " + opKind);
        };

        sb.append(operator).append(exprCode);
        return sb.toString();
    }


    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCCompilerException {
        // Retrieve the NameDef associated with this LValue
        NameDef nameDef = lValue.getNameDef();

        // Check if the NameDef is not null
        if (nameDef == null) {
            throw new PLCCompilerException("NameDef not found for LValue: " + lValue);
        }

        // Return the Java name associated with this LValue
        return nameDef.getJavaName();
    }


    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();

        // Generate code for the left-hand side (LValue)
        Object lValueCode = assignmentStatement.getlValue().visit(this, arg);
        if (lValueCode != null) {
            sb.append(lValueCode.toString());
        }

        // Add the assignment operator
        sb.append(" = ");

        // Evaluate the expression on the right-hand side
        Object exprCode = assignmentStatement.getE().visit(this, arg);
        if (exprCode != null) {
            sb.append(exprCode.toString());
        }

        // Add a semicolon to end the statement
        sb.append(";\n");

        return sb.toString();
    }


    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();

        // Generate code for the expression
        Object exprCode = writeStatement.getExpr().visit(this, arg);
        if (exprCode != null) {
            // Wrap the expression in a call to ConsoleIO.write()
            sb.append("ConsoleIO.write(").append(exprCode.toString()).append(");\n");
        }

        return sb.toString();
    }


    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        // Generate the code for the expression
        String exprCode = (String) returnStatement.getE().visit(this, arg);
        // Append the return statement to the code
        code.append("return ").append(exprCode).append(";\n");
        return code.toString();
    }





    @Override
    public Object visitBlockStatement(StatementBlock statementBlock, Object arg) throws PLCCompilerException {
        return statementBlock.getBlock().visit(this, arg);
    }






    @Override
    public Object visitChannelSelector(ChannelSelector channelSelector, Object arg) throws PLCCompilerException {
        return null;
    }


    @Override
    public Object visitConstExpr(ConstExpr constExpr, Object arg) throws PLCCompilerException {
        return null;
    }


    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitDoStatement(DoStatement doStatement, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitGuardedBlock(GuardedBlock guardedBlock, Object arg) throws PLCCompilerException {
        return null;
    }



    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws PLCCompilerException {
        return null;
    }




    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws PLCCompilerException {
        return null;
    }



}
