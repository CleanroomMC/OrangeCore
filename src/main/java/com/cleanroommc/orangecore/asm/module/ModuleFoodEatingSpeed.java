package com.cleanroommc.orangecore.asm.module;

import org.objectweb.asm.tree.*;
import com.cleanroommc.orangecore.asm.ASMConstants;
import com.cleanroommc.orangecore.asm.IClassTransformerModule;
import com.cleanroommc.airlock.api.asm.ASMHelper;
import com.cleanroommc.airlock.api.asm.ObfHelper;

import static org.objectweb.asm.Opcodes.*;

public class ModuleFoodEatingSpeed implements IClassTransformerModule
{
	@Override
	public String[] getClassesToTransform()
	{
		return new String[]{
		ASMConstants.ENTITY_LIVING,
		ASMConstants.ITEM_RENDERER
		};
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (transformedName.equals(ASMConstants.ENTITY_LIVING))
		{
			addItemInUseMaxDurationField(classNode);

			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_184598_c", "setActiveHand", ASMHelper.toMethodDescriptor("V", ASMConstants.HAND));
			if (methodNode != null)
			{
				patchSetActiveHand(classNode, methodNode);
			}
			else
				throw new RuntimeException(classNode.name + ": setActiveHand method not found");

			methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_184612_cw", "getItemInUseMaxCount", ASMHelper.toMethodDescriptor("I"));
			if (methodNode != null)
			{
				patchGetItemInUseMaxCount(classNode, methodNode);
			}
			else
				throw new RuntimeException(classNode.name + ": getItemInUseMaxCount method not found");
		}
		else if (transformedName.equals(ASMConstants.ITEM_RENDERER))
		{
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_187454_a", "transformEatFirstPerson", ASMHelper.toMethodDescriptor("V", "F", ASMConstants.HAND_SIDE, ASMConstants.ITEM_STACK));
			if (methodNode != null)
			{
				patchRenderItemInFirstPerson(methodNode);
			}
			else
				throw new RuntimeException(classNode.name + ": setActiveHand method not found");
		}

		return ASMHelper.writeClassToBytes(classNode);
	}

	private void patchRenderItemInFirstPerson(MethodNode method)
	{
		InsnList needle = new InsnList();
		needle.add(new VarInsnNode(ALOAD, 3));
		needle.add(new MethodInsnNode(INVOKEVIRTUAL, ASMHelper.toInternalClassName(ASMConstants.ITEM_STACK), ObfHelper.isObfuscated() ? "func_77988_m" : "getMaxItemUseDuration", ASMHelper.toMethodDescriptor("I"), false));

		InsnList replacement = new InsnList();
		replacement.add(new VarInsnNode(ALOAD, 0));
		replacement.add(new FieldInsnNode(GETFIELD, ObfHelper.getInternalClassName(ASMConstants.ITEM_RENDERER), ObfHelper.isObfuscated() ? "field_78455_a" : "mc", ASMHelper.toDescriptor(ASMConstants.MINECRAFT)));
		replacement.add(new FieldInsnNode(GETFIELD, ObfHelper.getInternalClassName(ASMConstants.MINECRAFT), ObfHelper.isObfuscated() ? "field_71439_g" : "player", ASMHelper.toDescriptor(ASMConstants.PLAYER_SP)));
		replacement.add(new FieldInsnNode(GETFIELD, ObfHelper.getInternalClassName(ASMConstants.PLAYER), "itemInUseMaxDuration", "I"));

		boolean replaced = ASMHelper.findAndReplace(method.instructions, needle, replacement) != null;
		if (!replaced)
			throw new RuntimeException("ItemRenderer.transformEatFirstPerson: no replacements made");
	}

	private void patchGetItemInUseMaxCount(ClassNode classNode, MethodNode method)
	{
		InsnList needle = new InsnList();
		needle.add(new VarInsnNode(ALOAD, 0));
		needle.add(new FieldInsnNode(GETFIELD, ObfHelper.getInternalClassName(ASMConstants.ENTITY_LIVING), ObfHelper.isObfuscated() ? "field_184627_bm" : "activeItemStack", ASMHelper.toDescriptor(ASMConstants.ITEM_STACK)));
		needle.add(new MethodInsnNode(INVOKEVIRTUAL, ObfHelper.getInternalClassName(ASMConstants.ITEM_STACK), ObfHelper.isObfuscated() ? "func_77988_m" : "getMaxItemUseDuration", ASMHelper.toMethodDescriptor("I"), false));

		InsnList replacement = new InsnList();
		replacement.add(new VarInsnNode(ALOAD, 0));
		replacement.add(new VarInsnNode(ALOAD, 0));
		replacement.add(new FieldInsnNode(GETFIELD, ASMHelper.toInternalClassName(classNode.name), "itemInUseMaxDuration", "I"));
		replacement.add(new MethodInsnNode(INVOKESTATIC, ASMHelper.toInternalClassName(ASMConstants.HOOKS), "getItemInUseMaxCount", ASMHelper.toMethodDescriptor("I", ASMConstants.ENTITY_LIVING, "I"), false));

		int numReplacementsMade = ASMHelper.findAndReplaceAll(method.instructions, needle, replacement);
		if (numReplacementsMade == 0)
			throw new RuntimeException("EntityLivingBase.getItemInUseMaxCount: no replacements made");
	}

	private void patchSetActiveHand(ClassNode classNode, MethodNode method)
	{
		AbstractInsnNode targetNode = ASMHelper.findFirstInstructionWithOpcode(method, PUTFIELD);
		while (targetNode != null && !((FieldInsnNode) targetNode).name.equals(ObfHelper.isObfuscated() ? "field_184628_bn" : "activeItemStackUseCount"))
		{
			targetNode = ASMHelper.findNextInstructionWithOpcode(targetNode, PUTFIELD);
		}

		if (targetNode == null)
			throw new RuntimeException("EntityLivingBase.setActiveHand: PUTFIELD activeItemStackUseCount instruction not found");

		InsnList toInject = new InsnList();

		toInject.add(new VarInsnNode(ALOAD, 0));
		toInject.add(new VarInsnNode(ILOAD, 3));
		toInject.add(new FieldInsnNode(PUTFIELD, ASMHelper.toInternalClassName(classNode.name), "itemInUseMaxDuration", "I"));

		method.instructions.insert(targetNode, toInject);
	}

	private void addItemInUseMaxDurationField(ClassNode classNode)
	{
		classNode.fields.add(new FieldNode(ACC_PUBLIC, "itemInUseMaxDuration", "I", null, null));
	}
}