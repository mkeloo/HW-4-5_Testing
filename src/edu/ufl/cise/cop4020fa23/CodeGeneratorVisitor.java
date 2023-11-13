package edu.ufl.cise.cop4020fa23;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;


import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;




public class CodeGeneratorVisitor implements ASTVisitor {

    // helper vars
    private Map<String, Integer> variableCounts = new HashMap<>();
    private SymbolTable symbolTable = new SymbolTable();


  /* ================================= MOKSH  ================================= */

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        String className = program.getName();
        Type returnType = program.getType();
        String javaReturnType = getJavaType(returnType);


        String packageName = "edu.ufl.cise.cop4020fa23";
        code.append(String.format("package %s;\n", packageName));
        code.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n\n");

        StringBuilder params = new StringBuilder();
        Map<String, String> paramMap = new HashMap<>();
        for (NameDef param : program.getParams()) {
            String originalName = param.getName();
            String paramName = isReservedKeyword(originalName) ? "param_" + originalName : originalName;
            String paramType = getJavaType(param.getType());
            String paramCode = String.format("%s %s", paramType, paramName);
            if (params.length() > 0) params.append(", ");
            params.append(paramCode);

            paramMap.put(originalName, paramName);
        }
        String blockCode = (String) program.getBlock().visit(this, paramMap);

        code.append(String.format("public class %s {\n", className));
        code.append(String.format("    public static %s apply(%s) {\n", javaReturnType, params));
        code.append(blockCode);
        code.append("    }\n");
        code.append("}\n");

        return code.toString();
    }
    private String getJavaType(Type type) {
        return switch (type) {
            case BOOLEAN -> "boolean";
            case INT -> "int";
            case STRING -> "String";
            default -> type.toString().toLowerCase();
        };
    }

    // list of reserved Java keywords
    private boolean isReservedKeyword(String name) {
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
                "true", "false", "null"
        );
        return reservedKeywords.contains(name);
    }


    @Override
    public Object visitBlock(Block block, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        code.append("{\n");
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
        String javaName = nameDef.getName();
        nameDef.setJavaName(javaName);
        code.append(javaType).append(" ").append(javaName);

        Dimension dimension = nameDef.getDimension();
        if (dimension != null) {
            String dimensionCode = (String) dimension.visit(this, arg);
            code.append(dimensionCode);
        }

        return code.toString();
    }


    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws TypeCheckException, PLCCompilerException {
        StringBuilder code = new StringBuilder();
        NameDef nameDef = declaration.getNameDef();
        String originalName = nameDef.getName();
        String scopedName = originalName;
        if (symbolTable.isDefinedInCurrentScope(originalName)) {
            scopedName = generateUniqueName(originalName);
        } else {
            try {
                symbolTable.insert(nameDef);
            } catch (TypeCheckException e) {
                System.err.println("Type check exception: " + e.getMessage());
            }
        }
        String nameDefCode = String.format("%s %s", getJavaType(nameDef.getType()), scopedName);
        code.append(nameDefCode);
        Expr initializer = declaration.getInitializer();
        if (initializer != null) {
            String exprCode = (String) initializer.visit(this, arg);
            code.append(" = ").append(exprCode);
        }
        code.append(";");
        return code.toString();
    }


    // helper method to generate unique var names
    private String generateUniqueName(String originalName) {
        int count = variableCounts.getOrDefault(originalName, 0);
        variableCounts.put(originalName, count + 1);
        return originalName + "_" + count;
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
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCCompilerException {
        return stringLitExpr.getText();
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCCompilerException {
        return numLitExpr.getText();
    }




    /* ================================= Daniel  ================================= */

    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws PLCCompilerException {
        return Boolean.parseBoolean(booleanLitExpr.getText()) ? "true" : "false";
    }


    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Object guardExprCode = conditionalExpr.getGuardExpr().visit(this, arg);
        sb.append("(").append(guardExprCode).append(" ? ");
        Object trueExprCode = conditionalExpr.getTrueExpr().visit(this, arg);
        sb.append(trueExprCode).append(" : ");
        Object falseExprCode = conditionalExpr.getFalseExpr().visit(this, arg);
        sb.append(falseExprCode).append(")");
        return sb.toString();
    }


    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Object leftExprCode = binaryExpr.getLeftExpr().visit(this, arg);
        Type leftExprType = binaryExpr.getLeftExpr().getType();
        Object rightExprCode = binaryExpr.getRightExpr().visit(this, arg);
        Kind opKind = binaryExpr.getOpKind();
        if (leftExprType == Type.STRING && opKind == Kind.EQ) {
            sb.append(leftExprCode).append(".equals(").append(rightExprCode).append(")");
        }
        else if (opKind == Kind.EXP) {
            sb.append("((int)Math.round(Math.pow(").append(leftExprCode).append(", ").append(rightExprCode).append(")))");
        }
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
        Object exprCode = unaryExpr.getExpr().visit(this, arg);
        Kind opKind = unaryExpr.getOp();
        String operator = switch (opKind) {
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
        if (lValue.getNameDef() != null) {
            return lValue.getNameDef().getJavaName();
        } else {
            if (arg instanceof Map) {
                Map<String, String> paramMap = (Map<String, String>) arg;
                String originalName = lValue.getName();
                String paramName = paramMap.getOrDefault(originalName, originalName);
                return paramName;
            } else {
                throw new PLCCompilerException("NameDef not found for LValue: " + lValue);
            }
        }
    }




    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Object lValueCode = assignmentStatement.getlValue().visit(this, arg);
        if (lValueCode != null) {
            sb.append(lValueCode.toString());
        }
        sb.append(" = ");
        Object exprCode = assignmentStatement.getE().visit(this, arg);
        if (exprCode != null) {
            sb.append(exprCode.toString());
        }
        sb.append(";\n");
        return sb.toString();
    }


    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Object exprCode = writeStatement.getExpr().visit(this, arg);
        if (exprCode != null) {
            sb.append("ConsoleIO.write(").append(exprCode.toString()).append(");\n");
        }
        return sb.toString();
    }


    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        String exprCode = (String) returnStatement.getE().visit(this, arg);
        code.append("return ").append(exprCode).append(";\n");
        return code.toString();
    }






    /* ================================= ASSIGNMENT 5 METHODS  ================================= */


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