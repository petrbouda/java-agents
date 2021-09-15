package pbouda.agents.core.bytecode.stackmanipulation;

import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

public class InvokeConstructor implements StackManipulation {

    private final Class<?> type;

    public InvokeConstructor(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor visitor, Implementation.Context context) {
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(type), "<init>", "()V", false);
        return StackSize.SINGLE.toDecreasingSize();
    }
}