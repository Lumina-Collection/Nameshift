/*
 * MIT License
 *
 * Copyright (c) 2023 Benjamin Selig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.luminacollection.nameshift.common.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;

import java.util.UUID;

public class LuckPermsHook
{
	private static LuckPermsHook instance;
	private final String metaKey = "nameshift-displayname";
	private LuckPerms luckPerms = null;
	
	private LuckPermsHook()
	{
	}
	
	public static LuckPermsHook instance()
	{
		return instance == null ? instance = new LuckPermsHook() : instance;
	}
	
	public void init(LuckPerms luckPerms)
	{
		this.luckPerms = luckPerms;
	}
	
	public void update(UUID uuid, String name)
	{
		if (luckPerms == null) return;
		if (!luckPerms.getUserManager().isLoaded(uuid))
			luckPerms.getUserManager().loadUser(uuid).thenRunAsync(() -> update(uuid, name));
		else
		{
			User user = luckPerms.getUserManager().getUser(uuid);
			assert user != null;
			var metaNode = MetaNode.builder(metaKey, name).build();
			user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(metaKey)));
			user.data().add(metaNode);
			luckPerms.getUserManager().saveUser(user);
		}
	}
}
