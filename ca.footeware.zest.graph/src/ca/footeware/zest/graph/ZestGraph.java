/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada. All rights
 * reserved. This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package ca.footeware.zest.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 * This snippet creates a very simple graph where Rock is connected to Paper which is connected to
 * scissors which is connected to rock. The nodes a layed out using a SpringLayout Algorithm, and
 * they can be moved around.
 * 
 * @author Ian Bull
 */
public class ZestGraph
{
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		// Create the shell
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Zest Graph");
		shell.setLayout(new GridLayout(2, true));
		shell.setSize(800, 800);
		
		final Color black = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		final Color red = Display.getDefault().getSystemColor(SWT.COLOR_RED);
		
		final Graph graph = new Graph(shell, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1)
				.applyTo(graph);
		
		graph.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(final MouseEvent e)
			{
				final Object source = e.getSource();
				if (source instanceof Graph)
				{
					final List<?> selection = ((Graph) source).getSelection();
					for (final Object object : selection)
					{
						final GraphNode node = (GraphNode) object;
						node.setBorderColor(node.getBorderColor() == black ? red : black);
					}
				}
			}
		});
		
		final GraphNode node0 = new GraphNode(graph, SWT.NONE, "Node 0");
		// final GraphNode node1 = new GraphNode(graph, SWT.NONE, "Node 1");
		// final GraphNode node2 = new GraphNode(graph, SWT.NONE, "Node 2");
		
		// new GraphConnection(graph, SWT.NONE, node0, node1);
		// new GraphConnection(graph, SWT.NONE, node1, node2);
		// new GraphConnection(graph, SWT.NONE, node2, node0);
		
		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
				true);
		
		final Button addButton = new Button(shell, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addButton);
		addButton.setText("Add Node");
		addButton.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.
			 * SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				addNode(graph);
			}
		});
		
		final Button layoutButton = new Button(shell, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(layoutButton);
		layoutButton.setText("Layout");
		layoutButton.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.
			 * SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				graph.applyLayout();
			}
		});
		
		shell.open();
		while (!shell.isDisposed())
		{
			while (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}
	
	/**
	 * Generates a random integer between 1 and provided maximum but not any of the values in
	 * provided list.
	 * 
	 * @param maxNumber
	 *            int
	 * @param numbersToAvoid
	 *            List {@link java.util.List}
	 * @param numbersToAvoid
	 */
	private static int getRandom(final int maxNumber, final List<Integer> numbersToAvoid)
	{
		System.err.println("\nmax=" + maxNumber);
		System.err.println("numbersToAvoid" + numbersToAvoid);
		int rand;
		do
		{
			rand = (int) Math.round(Math.random() * maxNumber);
		} while (numbersToAvoid.contains(new Integer(rand)));
		System.err.println("rand=" + rand + "\n");
		return rand;
	}
	
	/**
	 * @param graph
	 */
	static void addNode(final Graph graph)
	{
		// create new node at next index
		final List<?> nodes = graph.getNodes();
		List<Integer> avoidList = new ArrayList<Integer>();
		int i = 0;
		while (i < nodes.size())
		{
			avoidList.add(new Integer(i++));
		}
		
		// make new node
		System.err.println("creating new node...");
		final int newNodeIdx = getRandom(nodes.size(), avoidList);
		System.err.println("newNodeIdx=" + newNodeIdx);
		final GraphNode newNode = new GraphNode(graph, SWT.NONE, "node " + newNodeIdx);
		System.err.println(graph.getNodes());
		
		// make new connection
		System.err.println("creating new connection...");
		avoidList = new ArrayList<Integer>();
		avoidList.add(new Integer(newNodeIdx));
		final int firstConnIdx = getRandom(nodes.size() - 1, avoidList);
		System.err.println("firstConnIdx=" + firstConnIdx);
		System.err.println(nodes.get(firstConnIdx));
		new GraphConnection(graph, SWT.NONE, newNode, (GraphNode) nodes.get(firstConnIdx));
		
		// randomly make second new connection
		if (Math.random() > 0.5)
		{
			System.err.println("creating second connection");
			avoidList = new ArrayList<Integer>();
			avoidList.add(new Integer(newNodeIdx));
			avoidList.add(new Integer(firstConnIdx));
			final int secondConnIdx = getRandom(nodes.size() - 1, avoidList);
			System.err.println("secondConnIdx=" + secondConnIdx);
			System.err.println(nodes.get(secondConnIdx));
			new GraphConnection(graph, SWT.NONE, newNode, (GraphNode) nodes.get(secondConnIdx));
		}
		graph.applyLayout();
	}
}
