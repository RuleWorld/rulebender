package rulebender.contactmap.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rulebender.contactmap.models.Component;
import rulebender.contactmap.models.State;

/**
 * JUnit test suite for the Component class.
 * 
 * @author adammatthewsmith
 */
public class TestComponent 
{

	/**
	 * Tests that duplicate states will not be added.
	 */
	@Test
	public void TestStateUniqueness()
	{
		Component c = new Component("component");
		
		c.addState(new State("state"));
		c.addState(new State("state"));
		
		assertTrue(c.getStates().size() == 1);
	}
	
	/**
	 * Tests that states get merged.
	 */
	@Test
	public void TestMergeComponents()
	{
		// Create 1
		Component c1 = new Component("component1");
		c1.addState(new State("state1"));
				
		// Create 2
		Component c2 = new Component("component1");
		c2.addState(new State("state2"));
		
		// merge
		c1.mergeStates(c2);
		
		assertTrue(c1.getStates().size() == 2);
		assertTrue(c1.getStates().get(0).getName().equals("state1"));
		assertTrue(c1.getStates().get(1).getName().equals("state2"));
		
	}
}
