package com.sevensoupcans.sfsoftware.util.tile.pathfinding.node;

import java.util.Comparator;

public class NodeSortingComparator implements Comparator<Node> {

	@Override
	public int compare(Node o1, Node o2) 
	{
		return o1.compareTo(o2);
	}

}
