package edu.ufl.cise.cop4020fa23;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Set;


import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;


public class CodeGeneratorVisitor implements ASTVisitor {

    private Stack<Map<String, String>> scopeStack = new Stack<>();

    private SymbolTable symbolTable = new SymbolTable();


    @Override
    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();

        // Get the name of the program
        String className = program.getName();

        // Get the return type of the program
        Type returnType = program.getType();
        String javaReturnType = getJavaType(returnType);  // Convert to Java type

        // Adding package and import statements
        String packageName = "edu.ufl.cise.cop4020fa23";
        code.append(String.format("package %s;\n", packageName));
        code.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n\n"); // Import statement for ConsoleIO

        // Visit the parameters (name definitions)
        StringBuilder params = new StringBuilder();
        Map<String, String> paramMap = new HashMap<>(); // Create a map for parameter name mapping
        for (NameDef param : program.getParams()) {
            String originalName = param.getName();
            String paramName = isReservedKeyword(originalName) ? "param_" + originalName : originalName;
            String paramType = getJavaType(param.getType());  // Convert to Java type
            String paramCode = String.format("%s %s", paramType, paramName);
            if (params.length() > 0) params.append(", ");
            params.append(paramCode);

            paramMap.put(originalName, paramName); // Populate the paramMap
        }

        // Visit the block (body of the apply method) with paramMap
        String blockCode = (String) program.getBlock().visit(this, paramMap);

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
            case STRING: return "String";
            // Add other type conversions as needed
            default: return type.toString().toLowerCase();
        }
    }

    private boolean isReservedKeyword(String name) {
        // List of reserved Java keywords
        Set<String> reservedKeywords = Set.of(
                "abstract", "continue", "for", "new", "switch",
                "assert", "default", "goto", "package", "synchronized",
                "boolean", "do", "if", "private", "this",
                "break", "double", "implements", "protected", "throw",
                "byte", "else", "import", "public", "throws",
                "case", "enum", "instanceof", "return", "transient",
                "catch", "extends", "int", "short", "try",
                "char", "final", "interface", "static", "void",
                "class", "finally", "long", "strictfp", "volatile",
                "const", "float", "native", "super", "while",
                // Adding "true", "false", and "null" which are not technically keywords, but are literals
                "true", "false", "null"
        );
        return reservedKeywords.contains(name);
    }


    @Override
    public Object visitBlock(Block block, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        code.append("{\n");

        // Process each block element
        for (Block.BlockElem blockElem : block.getElems()) {
            String blockElemCode = (String) blockElem.visit(this, arg);
            code.append(blockElemCode);
        }

        code.append("}\n");
        return code.toString();
    }




    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();

        Map<Type, String> typeMapping = Map.of(
                Type.INT, "int",
                Type.BOOLEAN, "boolean",
                Type.STRING, "String",
                Type.VOID, "void",
                Type.IMAGE, "ImageType",
                Type.PIXEL, "PixelType"
        );

        Type type = nameDef.getType();
        String javaType = typeMapping.get(type);
        if (javaType == null) {
            throw new PLCCompilerException("Unsupported type: " + type);
        }

        // Set and append the Java name of the identifier
        String javaName = nameDef.getName(); // This gets the original name
        nameDef.setJavaName(javaName); // Ensure javaName is set in NameDef
        code.append(javaType).append(" ").append(javaName);

        Dimension dimension = nameDef.getDimension();
        if (dimension != null) {
            String dimensionCode = (String) dimension.visit(this, arg);
            code.append(dimensionCode);
        }

        return code.toString();
    }




//    @Override
//    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        NameDef nameDef = declaration.getNameDef();
//
//        // Generate code for the declaration
//        String nameDefCode = (String) nameDef.visit(this, arg);
//        code.append(nameDefCode);
//
//        // Handle initializer if present
//        Expr initializer = declaration.getInitializer();
//        if (initializer != null) {
//            String exprCode = (String) initializer.visit(this, arg);
//            code.append(" = ").append(exprCode);
//        }
//
//        code.append(";");
//        return code.toString();
//    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws TypeCheckException, PLCCompilerException {
        StringBuilder code = new StringBuilder();
        NameDef nameDef = declaration.getNameDef();

        // Check if name is already defined in the current scope
        String originalName = nameDef.getName();
        String scopedName = originalName;

        if (symbolTable.isDefinedInCurrentScope(originalName)) {
            // If already defined, generate a unique name
            scopedName = generateUniqueName(originalName);
        } else {
            // Insert into the symbol table if not already defined
            try {
                symbolTable.insert(nameDef);
            } catch (TypeCheckException e) {
                // Handle exception, perhaps by logging or rethrowing
                System.err.println("Type check exception: " + e.getMessage());
            }
        }

        // Generate code for the declaration
        String nameDefCode = String.format("%s %s", getJavaType(nameDef.getType()), scopedName);
        code.append(nameDefCode);

        // Handle initializer if present
        Expr initializer = declaration.getInitializer();
        if (initializer != null) {
            String exprCode = (String) initializer.visit(this, arg);
            code.append(" = ").append(exprCode);
        }

        code.append(";");
        return code.toString();
    }

    private int uniqueId = 0; // Member variable to generate unique names


    private String generateUniqueName(String baseName) {
        // Implement your logic to generate a unique name
        return baseName + "_" + uniqueId++; // Example implementation
    }


    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCCompilerException {
        return stringLitExpr.getText();
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCCompilerException {
        return numLitExpr.getText();
    }



    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
        if (arg instanceof Map) {
            Map<String, String> paramMap = (Map<String, String>) arg;
            String originalName = identExpr.getName();
            String paramName = paramMap.getOrDefault(originalName, originalName);
            return paramName;
        } else {
            return identExpr.getName();
        }
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

        // Get the operator kind and handle accordingly
        Kind opKind = unaryExpr.getOp();
        String operator = switch (opKind) {
            case PLUS -> "+";
            case MINUS -> "-";
            case BANG -> "!";
            // Add cases for other unary operators you support
            default -> throw new PLCCompilerException("Unsupported unary operator: " + opKind);
        };

        sb.append(operator).append(exprCode);
        return sb.toString();
    }




    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCCompilerException {
        // Check if the LValue already has a NameDef associated with it
        if (lValue.getNameDef() != null) {
            return lValue.getNameDef().getJavaName();
        } else {
            // If not, try to find the NameDef in the symbol table
            if (arg instanceof Map) {
                Map<String, String> paramMap = (Map<String, String>) arg;
                String originalName = lValue.getName();
                String paramName = paramMap.getOrDefault(originalName, originalName);
                return paramName;
            } else {
                // The variable was not found in the current scope.
                // This may indicate an undeclared variable or a scoping issue.
                throw new PLCCompilerException("NameDef not found for LValue: " + lValue);
            }
        }
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
