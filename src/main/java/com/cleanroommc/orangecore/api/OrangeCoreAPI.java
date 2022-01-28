package com.cleanroommc.orangecore.api;

/**
 * Used to access/mutate various hidden values of the hunger system or fire standard AppleCore events.
 * 
 * See {@link IOrangeCoreAccessor} and {@link IOrangeCoreMutator} for a list of the available functions.
 * {@link #accessor} and {@link #mutator} will be initialized by AppleCore on startup.
 */
public abstract class OrangeCoreAPI
{
	public static IOrangeCoreAccessor accessor;
	public static IOrangeCoreMutator mutator;
	public static IOrangeCoreRegistry registry;
}
