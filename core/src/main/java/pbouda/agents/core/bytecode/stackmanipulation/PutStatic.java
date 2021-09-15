package pbouda.agents.core.bytecode.stackmanipulation;

import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

public class PutStatic implements StackManipulation {

    private final String className;
    private final String fieldName;
    private final Class<?> fieldType;

    public PutStatic(String className, String fieldName, Class<?> fieldType) {
        this.className = className.replace(".", "/");
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor visitor, Implementation.Context context) {
        visitor.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, Type.getDescriptor(fieldType));
        return StackSize.SINGLE.toDecreasingSize();
    }
}