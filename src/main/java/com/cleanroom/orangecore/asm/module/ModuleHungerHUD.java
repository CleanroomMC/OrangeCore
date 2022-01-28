package com.cleanroom.orangecore.asm.module;

import org.objectweb.asm.tree.*;
import com.cleanroom.orangecore.asm.ASMConstants;
import com.cleanroom.orangecore.asm.IClassTransformerModule;
import com.cleanroommc.airlock.api.asm.ASMHelper;
import com.cleanroommc.airlock.api.asm.ObfHelper;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * Modify the hunger HUD rendering to draw hunger relative to AppleCore's variable max hunger
 */
public class ModuleHungerHUD implements IClassTransformerModule
{
	@Override
	public String[] getClassesToTransform()
	{
		return new String[]{ASMConstants.GUI_INGAME_FORGE};
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "renderFood", "(II)V");
		if (methodNode != null)
		{
			InsnList needle = new InsnList();
			needle.add(new MethodInsnNode(INVOKEVIRTUAL, ObfHelper.getInternalClassName(ASMConstants.FOOD_STATS), ObfHelper.isObfuscated() ? "func_75116_a" : "getFoodLevel", ASMHelper.toMethodDescriptor("I"), false));

			InsnList replace = new InsnList();
			replace.add(new MethodInsnNode(INVOKESTATIC, ASMConstants.HOOKS_INTERNAL_CLASS, "getHungerForDisplay", ASMHelper.toMethodDescriptor("I", ASMConstants.FOOD_STATS), false));

			AbstractInsnNode found = ASMHelper.findAndReplace(methodNode.instructions, needle, replace);

			if (found == null)
				throw new RuntimeException("GuiIngameForge: expected instruction pattern not found");

			return ASMHelper.writeClassToBytes(classNode);
		}
		else
			throw new RuntimeException("GuiIngameForge: renderFood method not found");
	}
}