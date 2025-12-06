package cn.Power.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class MotionBlurResourceManager implements IResourceManager {
	public Set getResourceDomains() {
		return null;
	}

	public IResource getResource(ResourceLocation location) throws IOException {
		return new MotionBlurResource();
	}

	public List getAllResources(ResourceLocation location) throws IOException {
		return null;
	}
}
