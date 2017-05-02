package net.bytebuddy.utility.visitor;

import net.bytebuddy.description.method.MethodDescription;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVariableAwareMethodVisitor extends MethodVisitor {

    private int offsetLimit, requiredOffset;

    public LocalVariableAwareMethodVisitor(MethodVisitor methodVisitor, MethodDescription instrumentedMethod) {
        super(Opcodes.ASM5, methodVisitor);
        requiredOffset = offsetLimit = instrumentedMethod.getStackSize();
    }

    @Override
    public void visitVarInsn(int opcode, int offset) {
        switch (opcode) {
            case Opcodes.ASTORE:
            case Opcodes.ISTORE:
            case Opcodes.FSTORE:
                offsetLimit = Math.max(offsetLimit, offset + 1);
                break;
            case Opcodes.LSTORE:
            case Opcodes.DSTORE:
                offsetLimit = Math.max(offsetLimit, offset + 2);
                break;
        }
        super.visitVarInsn(opcode, offset);
    }

    protected void requireMinimumOffset(int offset) {
        requiredOffset = Math.max(requiredOffset, offset);
    }

    protected int getOffsetLimit() {
        return offsetLimit;
    }

    @Override
    public void visitMaxs(int stackSize, int localVariableLength) {
        super.visitMaxs(stackSize, Math.max(localVariableLength, requiredOffset));
    }
}
