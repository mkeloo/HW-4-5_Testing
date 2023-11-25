package edu.ufl.cise.cop4020fa23;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;


import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;

import edu.ufl.cise.cop4020fa23.runtime.PixelOps;
import edu.ufl.cise.cop4020fa23.runtime.ImageOps;
import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;
import edu.ufl.cise.cop4020fa23.runtime.FileURLIO;
import edu.ufl.cise.cop4020fa23.runtime.PLCRuntimeException;


import edu.ufl.cise.cop4020fa23.Kind;




public class CodeGeneratorVisitor implements ASTVisitor {

    // helper vars
    private Map<String, Integer> variableCounts = new HashMap<>();
    private SymbolTable symbolTable = new SymbolTable();


  /* ================================= MOKSH  ================================= */

//    @Override
//    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        String className = program.getName();
//        Type returnType = program.getType();
//        String javaReturnType = getJavaType(returnType);
//
//
//        String packageName = "edu.ufl.cise.cop4020fa23";
//        code.append(String.format("package %s;\n", packageName));
//        code.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n\n");
//
//        StringBuilder params = new StringBuilder();
//        Map<String, String> paramMap = new HashMap<>();
//        for (NameDef param : program.getParams()) {
//            String originalName = param.getName();
//            String paramName = isReservedKeyword(originalName) ? "param_" + originalName : originalName;
//            String paramType = getJavaType(param.getType());
//            String paramCode = String.format("%s %s", paramType, paramName);
//            if (params.length() > 0) params.append(", ");
//            params.append(paramCode);
//
//            paramMap.put(originalName, paramName);
//        }
//        String blockCode = (String) program.getBlock().visit(this, paramMap);
//
//        code.append(String.format("public class %s {\n", className));
//        code.append(String.format("    public static %s apply(%s) {\n", javaReturnType, params));
//        code.append(blockCode);
//        code.append("    }\n");
//        code.append("}\n");
//
//        return code.toString();
//    }
//    private String getJavaType(Type type) {
//        return switch (type) {
//            case BOOLEAN -> "boolean";
//            case INT -> "int";
//            case STRING -> "String";
//            default -> type.toString().toLowerCase();
//        };
//    }

//    @Override
//    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        String className = program.getName();
//        Type returnType = program.getType();
//        String javaReturnType = getJavaType(returnType);
//
//        String packageName = "edu.ufl.cise.cop4020fa23";
//        code.append(String.format("package %s;\n", packageName));
//        code.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n\n");
//
//        StringBuilder params = new StringBuilder();
//        Map<String, String> paramMap = new HashMap<>();
//        for (NameDef param : program.getParams()) {
//            String originalName = param.getName();
//            String paramName = isReservedKeyword(originalName) ? "param_" + originalName : originalName;
//            String paramType = getJavaType(param.getType());
//            String paramCode = String.format("%s %s", paramType, paramName);
//            if (params.length() > 0) params.append(", ");
//            params.append(paramCode);
//
//            paramMap.put(originalName, paramName);
//        }
//        String blockCode = (String) program.getBlock().visit(this, paramMap);
//
//        code.append(String.format("public class %s {\n", className));
//        code.append(String.format("    public static %s apply(%s) {\n", javaReturnType, params));
//        code.append(blockCode);
//        code.append("    }\n");
//        code.append("}\n");
//
//        return code.toString();
//    }

//    @Override
//    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        String className = program.getName();
//        Type returnType = program.getType();
//        String javaReturnType = getJavaType(returnType);
//
//        String packageName = "edu.ufl.cise.cop4020fa23";
//        code.append(String.format("package %s;\n", packageName));
//        code.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n");
//        code.append("import edu.ufl.cise.cop4020fa23.runtime.PixelOps;\n"); // Add this line
//        code.append("import edu.ufl.cise.cop4020fa23.runtime.ImageOps;\n"); // Add this line for ImageOps
////        code.append("import edu.ufl.cise.cop4020fa23.runtime.FileURLIO;\n"); // Add this line for FileURLIO
//
//        StringBuilder params = new StringBuilder();
//        Map<String, String> paramMap = new HashMap<>();
//        for (NameDef param : program.getParams()) {
//            String originalName = param.getName();
//            String paramName = isReservedKeyword(originalName) ? "param_" + originalName : originalName;
//            String paramType = getJavaType(param.getType());
//            String paramCode = String.format("%s %s", paramType, paramName);
//            if (params.length() > 0) params.append(", ");
//            params.append(paramCode);
//
//            paramMap.put(originalName, paramName);
//        }
//        String blockCode = (String) program.getBlock().visit(this, paramMap);
//
//        code.append(String.format("public class %s {\n", className));
//        code.append(String.format("    public static %s apply(%s) {\n", javaReturnType, params));
//        code.append(blockCode);
//        code.append("    }\n");
//        code.append("}\n");
//
//        return code.toString();
//    }


//    CURRENT PROCESS
    @Override
    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        String className = program.getName();
        Type returnType = program.getType();
        String javaReturnType = getJavaType(returnType);

        String packageName = "edu.ufl.cise.cop4020fa23";
        code.append(String.format("package %s;\n", packageName));
        code.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n");
        code.append("import edu.ufl.cise.cop4020fa23.runtime.PixelOps;\n");
        code.append("import edu.ufl.cise.cop4020fa23.runtime.ImageOps;\n");
        // Include imports for BufferedImage and FileURLIO by default
        code.append("import java.awt.image.BufferedImage;\n");
        code.append("import edu.ufl.cise.cop4020fa23.runtime.FileURLIO;\n");

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
            case PIXEL -> "int"; // Add this line to handle PIXEL type
            case IMAGE -> "java.awt.image.BufferedImage"; // Correct mapping for IMAGE type
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


//    @Override
//    public Object visitDeclaration(Declaration declaration, Object arg) throws TypeCheckException, PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        NameDef nameDef = declaration.getNameDef();
//        String originalName = nameDef.getName();
//        String scopedName = originalName;
//        if (symbolTable.isDefinedInCurrentScope(originalName)) {
//            scopedName = generateUniqueName(originalName);
//        } else {
//            try {
//                symbolTable.insert(nameDef);
//            } catch (TypeCheckException e) {
//                System.err.println("Type check exception: " + e.getMessage());
//            }
//        }
//        String nameDefCode = String.format("%s %s", getJavaType(nameDef.getType()), scopedName);
//        code.append(nameDefCode);
//        Expr initializer = declaration.getInitializer();
//        if (initializer != null) {
//            String exprCode = (String) initializer.visit(this, arg);
//            code.append(" = ").append(exprCode);
//        }
//        code.append(";");
//        return code.toString();
//    }


//    @Override
//    public Object visitDeclaration(Declaration declaration, Object arg) throws TypeCheckException, PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        NameDef nameDef = declaration.getNameDef();
//        String originalName = nameDef.getName();
//        String scopedName = originalName;
//        if (symbolTable.isDefinedInCurrentScope(originalName)) {
//            scopedName = generateUniqueName(originalName);
//        } else {
//            try {
//                symbolTable.insert(nameDef);
//            } catch (TypeCheckException e) {
//                System.err.println("Type check exception: " + e.getMessage());
//            }
//        }
//        String nameDefCode = String.format("%s %s", getJavaType(nameDef.getType()), scopedName);
//        code.append(nameDefCode);
//        Expr initializer = declaration.getInitializer();
//        if (initializer != null) {
//            String exprCode = (String) initializer.visit(this, arg);
//            if (nameDef.getType() == Type.IMAGE && initializer.getType() == Type.STRING) {
//                // Special case for assigning a string URL to an image
//                code.append(" = FileURLIO.readImage(").append(exprCode).append(")");
//            } else {
//                code.append(" = ").append(exprCode);
//            }
//        }
//        code.append(";");
//        return code.toString();
//    }


//    SOLVED
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
        Dimension dimension = nameDef.getDimension();

        if (initializer != null) {
            String exprCode = (String) initializer.visit(this, arg);
            if (nameDef.getType() == Type.IMAGE) {
                if (dimension != null) {
                    // Resizing the image with specific dimensions
                    code.append(" = ImageOps.copyAndResize(").append(exprCode)
                            .append(", ").append(dimension.getWidth().visit(this, arg))
                            .append(", ").append(dimension.getHeight().visit(this, arg)).append(")");
                } else if (initializer.getType() == Type.STRING) {
                    // Loading an image from a URL
                    code.append(" = FileURLIO.readImage(").append(exprCode).append(")");
                } else {
                    // Regular image assignment
                    code.append(" = ").append(exprCode);
                }
            } else {
                // Handling non-image types
                code.append(" = ").append(exprCode);
            }
        }
        code.append(";");
        return code.toString();
    }


//    @Override
//    public Object visitDeclaration(Declaration declaration, Object arg) throws TypeCheckException, PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        NameDef nameDef = declaration.getNameDef();
//        String originalName = nameDef.getName();
//        String scopedName = originalName;
//        if (symbolTable.isDefinedInCurrentScope(originalName)) {
//            scopedName = generateUniqueName(originalName);
//        } else {
//            try {
//                symbolTable.insert(nameDef);
//            } catch (TypeCheckException e) {
//                System.err.println("Type check exception: " + e.getMessage());
//            }
//        }
//        String nameDefCode = String.format("%s %s", getJavaType(nameDef.getType()), scopedName);
//        code.append(nameDefCode);
//        Expr initializer = declaration.getInitializer();
//        Dimension dimension = nameDef.getDimension();
//
//        if (nameDef.getType() == Type.IMAGE) {
//            // Handling image declaration
//            if (dimension != null) {
//                // Initialize the image with specified dimensions
//                code.append(" = ImageOps.makeImage(")
//                        .append(dimension.getWidth().visit(this, arg))
//                        .append(", ")
//                        .append(dimension.getHeight().visit(this, arg))
//                        .append(")");
//            } else {
//                // If dimension is not specified, throw an exception
//                throw new PLCCompilerException("Image declaration without dimensions is not allowed");
//            }
//        } else if (initializer != null) {
//            // Handling non-image types
//            String exprCode = (String) initializer.visit(this, arg);
//            code.append(" = ").append(exprCode);
//        }
//        code.append(";");
//        return code.toString();
//    }



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


//    @Override
//    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
//        StringBuilder sb = new StringBuilder();
//        Object leftExprCode = binaryExpr.getLeftExpr().visit(this, arg);
//        Type leftExprType = binaryExpr.getLeftExpr().getType();
//        Object rightExprCode = binaryExpr.getRightExpr().visit(this, arg);
//        Kind opKind = binaryExpr.getOpKind();
//        if (leftExprType == Type.STRING && opKind == Kind.EQ) {
//            sb.append(leftExprCode).append(".equals(").append(rightExprCode).append(")");
//        }
//        else if (opKind == Kind.EXP) {
//            sb.append("((int)Math.round(Math.pow(").append(leftExprCode).append(", ").append(rightExprCode).append(")))");
//        }
//        else {
//            String operator = switch(opKind) {
//                case PLUS -> "+";
//                case MINUS -> "-";
//                case TIMES -> "*";
//                case DIV -> "/";
//                case MOD -> "%";
//                case AND -> "&&";
//                case OR -> "||";
//                case EQ -> "==";
//                case BANG -> "!";
//                case LT -> "<";
//                case GT -> ">";
//                case LE -> "<=";
//                case GE -> ">=";
//                default -> throw new PLCCompilerException("Unsupported binary operator: " + opKind);
//            };
//            sb.append("(").append(leftExprCode).append(" ").append(operator).append(" ").append(rightExprCode).append(")");
//        }
//        return sb.toString();
//    }


//    @Override
//    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
//        StringBuilder sb = new StringBuilder();
//        Object leftExprCode = binaryExpr.getLeftExpr().visit(this, arg);
//        Type leftExprType = binaryExpr.getLeftExpr().getType();
//        Object rightExprCode = binaryExpr.getRightExpr().visit(this, arg);
//        Type rightExprType = binaryExpr.getRightExpr().getType();
//        Kind opKind = binaryExpr.getOpKind();
//
//        if ((leftExprType == Type.PIXEL || rightExprType == Type.PIXEL) && opKind == Kind.PLUS) {
//            // Using ImageOps.binaryPackedPixelPixelOp for pixel addition
//            sb.append("ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.PLUS, ")
//                    .append(leftExprCode).append(", ").append(rightExprCode).append(")");
//        } else if (leftExprType == Type.STRING && opKind == Kind.EQ) {
//            sb.append(leftExprCode).append(".equals(").append(rightExprCode).append(")");
//        } else if (opKind == Kind.EXP) {
//            sb.append("((int)Math.round(Math.pow(").append(leftExprCode).append(", ")
//                    .append(rightExprCode).append(")))");
//        } else {
//            String operator = switch(opKind) {
//                case PLUS -> "+";
//                case MINUS -> "-";
//                case TIMES -> "*";
//                case DIV -> "/";
//                case MOD -> "%";
//                case AND -> "&&";
//                case OR -> "||";
//                case EQ -> "==";
//                case BANG -> "!";
//                case LT -> "<";
//                case GT -> ">";
//                case LE -> "<=";
//                case GE -> ">=";
//                default -> throw new PLCCompilerException("Unsupported binary operator: " + opKind);
//            };
//            sb.append("(").append(leftExprCode).append(" ").append(operator).append(" ")
//                    .append(rightExprCode).append(")");
//        }
//
//        return sb.toString();
//    }


//    @Override
//    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
//        StringBuilder sb = new StringBuilder();
//        Object leftExprCode = binaryExpr.getLeftExpr().visit(this, arg);
//        Type leftExprType = binaryExpr.getLeftExpr().getType();
//        Object rightExprCode = binaryExpr.getRightExpr().visit(this, arg);
//        Type rightExprType = binaryExpr.getRightExpr().getType();
//        Kind opKind = binaryExpr.getOpKind();
//
//        // Handling image and pixel binary operations
//        if (leftExprType == Type.IMAGE || rightExprType == Type.IMAGE) {
//            String imageOpMethod = switch(opKind) {
//                case PLUS -> "binaryImageImageOp";
//                case MINUS, TIMES, DIV, MOD -> // Appropriate methods for these operations
//                        throw new PLCCompilerException("Operation " + opKind + " not supported for images.");
//                default -> throw new PLCCompilerException("Unsupported binary operator for images: " + opKind);
//            };
//            sb.append("ImageOps.").append(imageOpMethod)
//                    .append("(ImageOps.OP.").append(opKind.name())
//                    .append(", ").append(leftExprCode).append(", ").append(rightExprCode).append(")");
//        } else if (leftExprType == Type.PIXEL && rightExprType == Type.PIXEL) {
//            // Handling binary operations between two pixels
//            sb.append("ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.")
//                    .append(opKind.name()).append(", ")
//                    .append(leftExprCode).append(", ").append(rightExprCode).append(")");
//        } else if (opKind == Kind.EQ && leftExprType == Type.STRING) {
//            // Handling string equality
//            sb.append(leftExprCode).append(".equals(").append(rightExprCode).append(")");
//        } else {
//            // Handling other binary expressions
//            String operator = switch(opKind) {
//                case PLUS -> "+";
//                case MINUS -> "-";
//                case TIMES -> "*";
//                case DIV -> "/";
//                case MOD -> "%";
//                case AND -> "&&";
//                case OR -> "||";
//                case EQ -> "==";
//                case BANG -> "!";
//                case LT -> "<";
//                case GT -> ">";
//                case LE -> "<=";
//                case GE -> ">=";
//                default -> throw new PLCCompilerException("Unsupported binary operator: " + opKind);
//            };
//            sb.append("(").append(leftExprCode).append(" ").append(operator).append(" ")
//                    .append(rightExprCode).append(")");
//        }
//
//        return sb.toString();
//    }


    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Object leftExprCode = binaryExpr.getLeftExpr().visit(this, arg);
        Type leftExprType = binaryExpr.getLeftExpr().getType();
        Object rightExprCode = binaryExpr.getRightExpr().visit(this, arg);
        Type rightExprType = binaryExpr.getRightExpr().getType();
        Kind opKind = binaryExpr.getOpKind();

        // Check if the operation is between two images
        if (leftExprType == Type.IMAGE && rightExprType == Type.IMAGE) {
            sb.append("ImageOps.binaryImageImageOp(ImageOps.OP.")
                    .append(opKind.name()).append(", ")
                    .append(leftExprCode).append(", ").append(rightExprCode).append(")");
        }
        // Check if the operation is between an image and a pixel
        else if (leftExprType == Type.IMAGE && rightExprType == Type.PIXEL ||
                leftExprType == Type.PIXEL && rightExprType == Type.IMAGE) {
            sb.append("ImageOps.binaryImagePixelOp(ImageOps.OP.")
                    .append(opKind.name()).append(", ")
                    .append(leftExprCode).append(", ").append(rightExprCode).append(")");
        }
        // Check if the operation is between an image and an int
        else if (leftExprType == Type.IMAGE && rightExprType == Type.INT) {
            sb.append("ImageOps.binaryImageScalarOp(ImageOps.OP.")
                    .append(opKind.name()).append(", ")
                    .append(leftExprCode).append(", ").append(rightExprCode).append(")");
        }
        // Check if the operation is between two pixels
        else if (leftExprType == Type.PIXEL && rightExprType == Type.PIXEL) {
            sb.append("ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.")
                    .append(opKind.name()).append(", ")
                    .append(leftExprCode).append(", ").append(rightExprCode).append(")");
        }
        // String equality check
        else if (opKind == Kind.EQ && leftExprType == Type.STRING) {
            sb.append(leftExprCode).append(".equals(").append(rightExprCode).append(")");
        }
        // Other binary operations
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
            sb.append("(").append(leftExprCode).append(" ").append(operator).append(" ")
                    .append(rightExprCode).append(")");
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




//    PASS TEST CASE 6 BUT FAILS TEST CASE 9
//    @Override
//    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
//        StringBuilder sb = new StringBuilder();
//        Object lValueCode = assignmentStatement.getlValue().visit(this, arg);
//        if (lValueCode != null) {
//            sb.append(lValueCode.toString());
//        }
//        sb.append(" = ");
//        Object exprCode = assignmentStatement.getE().visit(this, arg);
//        if (exprCode != null) {
//            sb.append(exprCode.toString());
//        }
//        sb.append(";\n");
//        return sb.toString();
//    }

//    PASSES TEST CASE 9 BUT FAILS TEST CASE 6
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        LValue lValue = assignmentStatement.getlValue();
        Expr expr = assignmentStatement.getE();

        Object lValueCode = lValue.visit(this, arg);
        Object exprCode = expr.visit(this, arg);

        if (lValue instanceof LValue) {
            LValue lValueWithSelector = (LValue) lValue;
            ChannelSelector channelSelector = lValueWithSelector.getChannelSelector();
            if (channelSelector != null) {
                Kind channelKind = channelSelector.color();
                switch (channelKind) {
                    case RES_red:
                        sb.append(lValueCode).append(" = PixelOps.setRed(").append(lValueCode).append(", ").append(exprCode).append(");");
                        break;
                    case RES_green:
                        sb.append(lValueCode).append(" = PixelOps.setGreen(").append(lValueCode).append(", ").append(exprCode).append(");");
                        break;
                    case RES_blue:
                        sb.append(lValueCode).append(" = PixelOps.setBlue(").append(lValueCode).append(", ").append(exprCode).append(");");
                        break;
                    default:
                        throw new PLCCompilerException("Unsupported channel selector: " + channelKind);
                }
            } else {
                sb.append(lValueCode).append(" = ").append(exprCode).append(";");
            }
        } else {
            sb.append(lValueCode).append(" = ").append(exprCode).append(";");
        }

        sb.append("\n");
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


//    @Override
//    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        String exprCode = (String) returnStatement.getE().visit(this, arg);
//        code.append("return ").append(exprCode).append(";\n");
//        return code.toString();
//    }


    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        Expr expr = returnStatement.getE();
        String exprCode; // Declare the variable

        // Check if the returned expression is a pixel component access
        if (expr instanceof PostfixExpr) {
            PostfixExpr postfixExpr = (PostfixExpr) expr;
            ChannelSelector channelSelector = postfixExpr.channel();
            if (channelSelector != null) {
                // Extract the channel from the pixel and handle as integer return
                String channelMethod = switch (channelSelector.color()) {
                    case RES_red -> "red";
                    case RES_green -> "green";
                    case RES_blue -> "blue";
                    default -> throw new PLCCompilerException("Unsupported channel selector: " + channelSelector);
                };
                String primaryExprCode = (String) postfixExpr.primary().visit(this, arg);
                exprCode = "PixelOps." + channelMethod + "(" + primaryExprCode + ")";
            } else {
                // For other expressions, visit normally
                exprCode = (String) expr.visit(this, arg);
            }
        } else {
            // For other expressions, visit normally
            exprCode = (String) expr.visit(this, arg);
        }

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
        String constName = constExpr.getName();
        if (constName.equals("Z")) {
            return "255"; // Return as a string since it's an integer literal
        } else {
            try {
                java.awt.Color color = (java.awt.Color) java.awt.Color.class.getField(constName).get(null);
                return Integer.toString(color.getRGB()); // Return as a string representing the integer value
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new PLCCompilerException("Unsupported constant: " + constName);
            }
        }
    }



    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCCompilerException {
        return null;
    }

//    @Override
//    public Object visitDoStatement(DoStatement doStatement, Object arg) throws PLCCompilerException {
//        StringBuilder code = new StringBuilder();
//        code.append("do {\n");
//        boolean first = true;
//        for (GuardedBlock gBlock : doStatement.getGuardedBlocks()) {
//            if (!first) {
//                code.append(" else ");
//            }
//            code.append("if (");
//            String guardCode = (String) gBlock.getGuard().visit(this, arg);
//            code.append(guardCode).append(") ");
//            code.append(gBlock.getBlock().visit(this, arg));
//            first = false;
//        }
//        code.append("} while (");
//        // Check for the termination condition of the loop
//        // The loop continues until all guards are false
//        first = true;
//        for (GuardedBlock gBlock : doStatement.getGuardedBlocks()) {
//            if (!first) {
//                code.append(" || ");
//            }
//            String guardCode = (String) gBlock.getGuard().visit(this, arg);
//            code.append("!").append(guardCode);
//            first = false;
//        }
//        code.append(");\n");
//        return code.toString();
//    }


    @Override
    public Object visitDoStatement(DoStatement doStatement, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        code.append("do {\n");
        for (GuardedBlock gBlock : doStatement.getGuardedBlocks()) {
            code.append("if (");
            String guardCode = (String) gBlock.getGuard().visit(this, arg);
            code.append(guardCode).append(") ");
            code.append(gBlock.getBlock().visit(this, arg));
        }
        code.append(" else if (a == b) {"); // Handling the case when a and b are equal
        code.append("break;"); // Break out of the loop when a and b are equal
        code.append("}\n");
        code.append("} while (a != 0 && b != 0);\n");
        return code.toString();
    }


    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCCompilerException {
        Object redComponent = expandedPixelExpr.getRed().visit(this, arg);
        Object greenComponent = expandedPixelExpr.getGreen().visit(this, arg);
        Object blueComponent = expandedPixelExpr.getBlue().visit(this, arg);

        return "PixelOps.pack(" + redComponent + ", " + greenComponent + ", " + blueComponent + ")";
    }


    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        code.append("if (");
        boolean first = true;
        for (GuardedBlock gBlock : ifStatement.getGuardedBlocks()) {
            if (!first) {
                code.append(" else if (");
            }
            String guardCode = (String) gBlock.getGuard().visit(this, arg);
            code.append(guardCode).append(") ");
            code.append(gBlock.getBlock().visit(this, arg));
            first = false;
        }
        code.append(" else {\n}\n"); // Add an empty else block to handle the '[]' case
        return code.toString();
    }

    @Override
    public Object visitGuardedBlock(GuardedBlock guardedBlock, Object arg) throws PLCCompilerException {
        StringBuilder code = new StringBuilder();
        String blockCode = (String) guardedBlock.getBlock().visit(this, arg);
        code.append("{\n").append(blockCode).append("}\n");
        return code.toString();
    }



    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Object primaryExprCode = postfixExpr.primary().visit(this, arg);
        Type primaryExprType = postfixExpr.primary().getType();
        PixelSelector pixelSelector = postfixExpr.pixel();
        ChannelSelector channelSelector = postfixExpr.channel();

        if (primaryExprType == Type.PIXEL) {
            if (channelSelector != null) {
                // Using Kind enum to determine the color method
                Kind color = channelSelector.color();
                String channelMethod = switch (color) {
                    case RES_red -> "red";
                    case RES_green -> "green";
                    case RES_blue -> "blue";
                    default -> throw new PLCCompilerException("Unsupported channel selector: " + color);
                };
                sb.append("PixelOps.").append(channelMethod).append("(").append(primaryExprCode).append(")");
            } else {
                // Handle case with only a pixel without a channel selector
                sb.append(primaryExprCode);
            }
        } else if (primaryExprType == Type.IMAGE) {
            if (pixelSelector != null && channelSelector == null) {
                // Handle image with pixel selector only
                sb.append("ImageOps.getRGB(").append(primaryExprCode).append(", ").append(pixelSelector.visit(this, arg)).append(")");
            } else if (pixelSelector != null && channelSelector != null) {
                // Handle image with both pixel and channel selectors
                Kind color = channelSelector.color();
                String channelMethod = switch (color) {
                    case RES_red -> "red";
                    case RES_green -> "green";
                    case RES_blue -> "blue";
                    default -> throw new PLCCompilerException("Unsupported channel selector: " + color);
                };
                sb.append("PixelOps.").append(channelMethod).append("(ImageOps.getRGB(").append(primaryExprCode).append(", ").append(pixelSelector.visit(this, arg)).append("))");
            } else if (channelSelector != null) {
                // Handle image with channel selector only
                Kind color = channelSelector.color();
                String extractMethod = switch (color) {
                    case RES_red -> "extractRed";
                    case RES_green -> "extractGreen";
                    case RES_blue -> "extractBlue";
                    default -> throw new PLCCompilerException("Unsupported channel selector: " + color);
                };
                sb.append("ImageOps.").append(extractMethod).append("(").append(primaryExprCode).append(")");
            } else {
                // Handle image without selectors
                sb.append(primaryExprCode);
            }
        }

        return sb.toString();
    }


}