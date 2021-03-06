package com.cleanroommc.orangecore.asm.module;

import org.objectweb.asm.tree.*;
import com.cleanroommc.orangecore.asm.ASMConstants;
import com.cleanroommc.orangecore.asm.IClassTransformerModule;
import com.cleanroommc.airlock.api.asm.ASMHelper;
import com.cleanroommc.airlock.api.asm.ObfHelper;

import static org.objectweb.asm.Opcodes.*;

public class ModulePeacefulRegen implements IClassTransformerModule
{
	@Override
	public String[] getClassesToTransform()
	{
		return new String[]{ASMConstants.PLAYER};
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals(ASMConstants.PLAYER))
		{
			ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_70636_d", "onLivingUpdate", ASMHelper.toMethodDescriptor("V"));
			if (methodNode != null)
			{
				addPeacefulRegenHook(classNode, methodNode);
				addPeacefulHungerRegenHook(classNode, methodNode);
				return ASMHelper.writeClassToBytes(classNode);
			}
			else
				throw new RuntimeException("EntityPlayer: onLivingUpdate method not found");
		}
		return basicClass;
	}

	public void addPeacefulRegenHook(ClassNode classNode, MethodNode method)
	{
		AbstractInsnNode relevantParentConditional = ASMHelper.find(method.instructions, new LdcInsnNode("naturalRegeneration"));
		AbstractInsnNode relevantConditional = ASMHelper.find(relevantParentConditional, new MethodInsnNode(INVOKEVIRTUAL, ASMHelper.toInternalClassName(ASMConstants.PLAYER), ObfHelper.isObfuscated() ? "func_110143_aJ" : "getHealth", ASMHelper.toMethodDescriptor("F"), false));
		JumpInsnNode ifNode = (JumpInsnNode) ASMHelper.find(relevantConditional, new JumpInsnNode(IFNE, new LabelNode()));
		LabelNode ifBlockEndLabel = ifNode.label;
		AbstractInsnNode targetNode = ASMHelper.find(ifNode, new InsnNode(FCONST_1)).getPrevious();

		int peacefulRegenEventIndex = firePeacefulRegenEventAndStoreEventBefore(method, targetNode, ifBlockEndLabel);

		InsnList healAmountNeedle = new InsnList();
		healAmountNeedle.add(new InsnNode(FCONST_1));

		InsnList healAmountReplacement = new InsnList();
		healAmountReplacement.add(new VarInsnNode(ALOAD, peacefulRegenEventIndex));
		healAmountReplacement.add(new FieldInsnNode(GETFIELD, ASMHelper.toInternalClassName(ASMConstants.HealthRegenEvent.PEACEFUL_REGEN), "deltaHealth", "F"));

		ASMHelper.findAndReplace(method.instructions, healAmountNeedle, healAmountReplacement, targetNode);

		InsnList ifNotCanceledBlock = new InsnList();
		LabelNode ifNotCanceled = new LabelNode();

		ifNotCanceledBlock.add(new VarInsnNode(ALOAD, peacefulRegenEventIndex));
		ifNotCanceledBlock.add(new MethodInsnNode(INVOKEVIRTUAL, ASMHelper.toInternalClassName(ASMConstants.HealthRegenEvent.PEACEFUL_REGEN), "isCanceled", ASMHelper.toMethodDescriptor("Z"), false));
		ifNotCanceledBlock.add(new JumpInsnNode(IFNE, ifNotCanceled));
		method.instructions.insertBefore(targetNode, ifNotCanceledBlock);

		method.instructions.insertBefore(ifBlockEndLabel, ifNotCanceled);
	}

	private int firePeacefulRegenEventAndStoreEventBefore(MethodNode method, AbstractInsnNode injectPoint, LabelNode endLabel)
	{
		// create  variable
		LabelNode peacefulRegenEventStart = new LabelNode();
		LocalVariableNode peacefulRegenEvent = new LocalVariableNode("peacefulRegenEvent", ASMHelper.toDescriptor(ASMConstants.HealthRegenEvent.PEACEFUL_REGEN), null, peacefulRegenEventStart, endLabel, method.maxLocals);
		method.maxLocals += 1;
		method.localVariables.add(peacefulRegenEvent);

		InsnList toInject = new InsnList();

		// HealthRegenEvent.PeacefulRegen peacefulRegenEvent = Hooks.firePeacefulRegenEvent(this);
		toInject.add(new VarInsnNode(ALOAD, 0));
		toInject.add(new MethodInsnNode(INVOKESTATIC, ASMHelper.toInternalClassName(ASMConstants.HOOKS), "firePeacefulRegenEvent", ASMHelper.toMethodDescriptor(ASMConstants.HealthRegenEvent.PEACEFUL_REGEN, ASMConstants.PLAYER), false));
		toInject.add(new VarInsnNode(ASTORE, peacefulRegenEvent.index));
		toInject.add(peacefulRegenEventStart);

		method.instructions.insertBefore(injectPoint, toInject);

		return peacefulRegenEvent.index;
	}

	public void addPeacefulHungerRegenHook(ClassNode classNode, MethodNode method)
	{
		AbstractInsnNode relevantParentConditional = ASMHelper.find(method.instructions, new LdcInsnNode("naturalRegeneration"));
		AbstractInsnNode relevantConditional = ASMHelper.find(relevantParentConditional, new MethodInsnNode(INVOKEVIRTUAL, ASMHelper.toInternalClassName(ASMConstants.FOOD_STATS), ObfHelper.isObfuscated() ? "func_75121_c" : "needFood", "()Z", false));
		JumpInsnNode ifNode = (JumpInsnNode) ASMHelper.find(relevantConditional, new JumpInsnNode(IFNE, new LabelNode()));
		LabelNode ifBlockEndLabel = ifNode.label;
		AbstractInsnNode targetNode = ASMHelper.findNextInstruction(ifNode);

		int peacefulRegenEventIndex = firePeacefulHungerRegenEventAndStoreEventBefore(method, targetNode, ifBlockEndLabel);

		InsnList healAmountNeedle = new InsnList();
		healAmountNeedle.add(new InsnNode(ICONST_1));

		InsnList healAmountReplacement = new InsnList();
		healAmountReplacement.add(new VarInsnNode(ALOAD, peacefulRegenEventIndex));
		healAmountReplacement.add(new FieldInsnNode(GETFIELD, ASMHelper.toInternalClassName(ASMConstants.HungerRegenEvent.PEACEFUL_REGEN), "deltaHunger", "I"));

		ASMHelper.findAndReplace(method.instructions, healAmountNeedle, healAmountReplacement, targetNode);

		InsnList ifNotCanceledBlock = new InsnList();
		LabelNode ifNotCanceled = new LabelNode();

		ifNotCanceledBlock.add(new VarInsnNode(ALOAD, peacefulRegenEventIndex));
		ifNotCanceledBlock.add(new MethodInsnNode(INVOKEVIRTUAL, ASMHelper.toInternalClassName(ASMConstants.HungerRegenEvent.PEACEFUL_REGEN), "isCanceled", ASMHelper.toMethodDescriptor("Z"), false));
		ifNotCanceledBlock.add(new JumpInsnNode(IFNE, ifNotCanceled));
		method.instructions.insertBefore(targetNode, ifNotCanceledBlock);

		method.instructions.insertBefore(ifBlockEndLabel, ifNotCanceled);
	}

	private int firePeacefulHungerRegenEventAndStoreEventBefore(MethodNode method, AbstractInsnNode injectPoint, LabelNode endLabel)
	{
		// create  variable
		LabelNode peacefulRegenEventStart = new LabelNode();
		LocalVariableNode peacefulRegenEvent = new LocalVariableNode("peacefulHungerRegenEvent", ASMHelper.toDescriptor(ASMConstants.HungerRegenEvent.PEACEFUL_REGEN), null, peacefulRegenEventStart, endLabel, method.maxLocals);
		method.maxLocals += 1;
		method.localVariables.add(peacefulRegenEvent);

		InsnList toInject = new InsnList();

		// HealthRegenEvent.PeacefulRegen peacefulRegenEvent = Hooks.firePeacefulRegenEvent(this);
		toInject.add(new VarInsnNode(ALOAD, 0));
		toInject.add(new MethodInsnNode(INVOKESTATIC, ASMHelper.toInternalClassName(ASMConstants.HOOKS), "firePeacefulHungerRegenEvent", ASMHelper.toMethodDescriptor(ASMConstants.HungerRegenEvent.PEACEFUL_REGEN, ASMConstants.PLAYER), false));
		toInject.add(new VarInsnNode(ASTORE, peacefulRegenEvent.index));
		toInject.add(peacefulRegenEventStart);

		method.instructions.insertBefore(injectPoint, toInject);

		return peacefulRegenEvent.index;
	}
}