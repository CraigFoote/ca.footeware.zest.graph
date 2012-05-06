package ca.footeware.zest.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 * This snippet shows how to use the IGraphContentProvider to create a graph with Zest. In this
 * example, getElements returns 3 edges: * Rock2Paper * Paper2Scissors * Scissors2Rock And for each
 * of these, the source and destination are returned in getSource and getDestination. A label
 * provider is also used to create the text and icons for the graph.
 * 
 * @author Ian Bull
 */
public class ZestGraphViewer
{
	private static final String nodePrefix = "node";
	private static final String edgeConjunction = " to ";
	
	private static class MyContentProvider implements IGraphContentProvider
	{
		public Object getSource(Object rel)
		{
			if (rel instanceof String)
			{
				String edgeName = (String) rel;
				if (edgeName.contains(edgeConjunction))
				{
					return edgeName.substring(0, edgeName.indexOf(nodePrefix));
				}
			}
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object input)
		{
			System.err.println("getElements: " + input);
			return ((List<String>) input).toArray();
		}
		
		public Object getDestination(Object rel)
		{
			if (rel instanceof String)
			{
				String edgeName = (String) rel;
				if (edgeName.contains(edgeConjunction))
				{
					return edgeName.substring(edgeName.indexOf(edgeConjunction));
				}
			}
			return rel;
		}
		
		public double getWeight(Object connection)
		{
			return 0;
		}
		
		public void dispose()
		{
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			System.err.println("inputChanged: " + newInput);
		}
		
	}
	
	static class MyLabelProvider extends LabelProvider
	{
		final Image image = Display.getDefault().getSystemImage(SWT.ICON_INFORMATION);
		
		public Image getImage(Object element)
		{
			return image;
		}
		
		public String getText(Object element)
		{
			return element.toString();
		}
		
	}
	
	private static GraphViewer viewer = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphJFaceSnippet2");
		shell.setLayout(new GridLayout(2, true));
		shell.setSize(800, 800);
		
		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		viewer.setInput(new ArrayList());
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1)
				.applyTo(viewer.getControl());
		
		Button addButton = new Button(shell, SWT.PUSH);
		addButton.setText("Add Node");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addButton);
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
				addNode();
			}
		});
		
		Button layoutButton = new Button(shell, SWT.PUSH);
		layoutButton.setText("Lay Out");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(layoutButton);
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
				viewer.applyLayout();
			}
		});
		
		shell.open();
		while (!shell.isDisposed())
		{
			while (!d.readAndDispatch())
			{
				d.sleep();
			}
		}
	}
	
	static void addNode()
	{
		// create new node at next index
		Object[] nodes = viewer.getNodeElements();
		List<Integer> avoidList = new ArrayList<Integer>();
		int i = 0;
		while (i < nodes.length)
		{
			avoidList.add(new Integer(i++));
		}
		
		// make new node
		System.err.println("creating new node...");
		List<String> input = (List<String>) viewer.getInput();
		System.err.println("input=" + input);
		System.err.println("size=" + input.size());
		if (input.size() == 0)
		{
			input.add(nodePrefix + input.size());
		}
		else
		{
			input.add(nodePrefix + input.size() + " to " + nodePrefix + ((input.size() - 1)));
		}
		viewer.refresh();
		// final int newNodeIdx = getRandom(nodes.length, avoidList);
		// System.err.println("newNodeIdx=" + newNodeIdx);
		// final GraphNode newNode = new GraphNode(viewer, SWT.NONE, "node " + newNodeIdx);
		// System.err.println(graph.getNodes());
		//
		// // make new connection
		// System.err.println("creating new connection...");
		// avoidList = new ArrayList<Integer>();
		// avoidList.add(new Integer(newNodeIdx));
		// final int firstConnIdx = getRandom(nodes.size() - 1, avoidList);
		// System.err.println("firstConnIdx=" + firstConnIdx);
		// System.err.println(nodes.get(firstConnIdx));
		// new GraphConnection(graph, SWT.NONE, newNode, (GraphNode) nodes.get(firstConnIdx));
		//
		// // randomly make second new connection
		// if (Math.random() > 0.5)
		// {
		// System.err.println("creating second connection");
		// avoidList = new ArrayList<Integer>();
		// avoidList.add(new Integer(newNodeIdx));
		// avoidList.add(new Integer(firstConnIdx));
		// final int secondConnIdx = getRandom(nodes.size() - 1, avoidList);
		// System.err.println("secondConnIdx=" + secondConnIdx);
		// System.err.println(nodes.get(secondConnIdx));
		// new GraphConnection(graph, SWT.NONE, newNode, (GraphNode) nodes.get(secondConnIdx));
		// }
		viewer.applyLayout();
	}
	
	/**
	 * Generates a random integer between 0 and provided maximum execpt any of the values in
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
}
