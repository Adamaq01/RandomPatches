package com.therandomlabs.randompatches.core;

import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public abstract class Patch {
	public final String hookClass =
			getName(getClass()).replaceAll("patch/(.+)Patch", "hook/$1Hook");

	public abstract boolean apply(ClassNode node);

	public boolean computeFrames() {
		return false;
	}

	public final String getHookInnerClass(String name) {
		return hookClass + "$" + name;
	}

	public static MethodNode findMethod(ClassNode node, String name) {
		return findMethod(node, name, name);
	}

	public static MethodNode findMethod(ClassNode node, String name, String srgName) {
		return findMethod(node, name, srgName, "");
	}

	public static MethodNode findMethod(ClassNode node, String name, String srgName, String desc) {
		final String methodName = getName(name, srgName);

		for (MethodNode method : node.methods) {
			if (methodName.equals(method.name) && (desc.isEmpty() || desc.equals(method.desc))) {
				RandomPatches.LOGGER.debug("Patching method: " + method.name + " (" + name + ")");
				return method;
			}
		}

		return null;
	}

	public static InsnList findInstructions(ClassNode node, String name) {
		final MethodNode method = findMethod(node, name);
		return method == null ? null : method.instructions;
	}

	public static InsnList findInstructions(ClassNode node, String name, String srgName) {
		final MethodNode method = findMethod(node, name, srgName);
		return method == null ? null : method.instructions;
	}

	public static InsnList findInstructions(
			ClassNode node, String name, String srgName, String desc
	) {
		final MethodNode method = findMethod(node, name, srgName, desc);
		return method == null ? null : method.instructions;
	}

	public static String getName(String name, String srgName) {
		return RandomPatches.IS_DEOBFUSCATED ? name : srgName;
	}

	public static String getName(Class<?> clazz) {
		return Type.getInternalName(clazz);
	}
}
