package pbouda.agents.core.bytecode.appender;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.MethodVisitor;
import pbouda.agents.core.bytecode.stackmanipulation.InvokeConstructor;
import pbouda.agents.core.bytecode.stackmanipulation.NewType;
import pbouda.agents.core.bytecode.stackmanipulation.PutStatic;

import java.util.concurrent.ConcurrentHashMap;

public class StaticFieldAssigner implements ByteCodeAppender {

    private final String className;
    private final String fieldName;
    private final Class<?> fieldType;

    public StaticFieldAssigner(String className, String fieldName, Class<?> fieldType) {
        this.className = className;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public Size apply(MethodVisitor visitor, Implementation.Context context, MethodDescription methodDesc) {
        StackManipulation.Size operandStackSize = new StackManipulation.Compound(
                new NewType(ConcurrentHashMap.class),
                Duplication.SINGLE,
                new InvokeConstructor(ConcurrentHashMap.class),
                new PutStatic(className, fieldName, fieldType)
        ).apply(visitor, context);
        return new Size(operandStackSize.getMaximalSize(), methodDesc.getStackSize());
    }
}