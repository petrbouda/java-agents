package pbouda.agents.core.bytecode.stackmanipulation;

import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

public class NewType implements StackManipulation {

    private final Class<?> type;

    public NewType(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor visitor, Implementation.Context context) {
        visitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(type));
        return StackSize.SINGLE.toIncreasingSize();
    }
}