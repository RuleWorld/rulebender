package rulebender.contactmap.models;

/**
 * The bond class represents a chemical bond between two molecules in the model.
 */
public class Bond 
{
	// The integer id of the first molecule. 
	private int molecule1;

	// The integer id of the first component.
	private int component1;
	
	// The integer id of the first state.
	private int state1;
	
	// The integer id of the second molecule.
	private int molecule2;

	// The integer id of the second component.
	private int component2;
	
	// The integer id of the second state.
	private int state2;
	
	// This is not used at the moment, but might be important for the influence graph.
	//private boolean CanGenerate;
	
	public Bond(int moleIndex1, int compIndex1, int stateIndex1, int moleIndex2, int compIndex2, int stateIndex2)
	{
		setMolecule1(moleIndex1);
		setComponent1(compIndex1);
		setState1(stateIndex1);
		setMolecule2(moleIndex2);
		setComponent2(compIndex2);
		setState2(stateIndex2);
		//setCanGenerate(false);
	}

	/*
	public void setCanGenerate(boolean canGenerate) {
		CanGenerate = canGenerate;
	}
   */
	/*
	public boolean isCanGenerate() {
		return CanGenerate;
	}
	*/

	public void setMolecule1(int molecule1) {
		this.molecule1 = molecule1;
	}

	public int getMolecule1() {
		return molecule1;
	}

	public void setComponent1(int component1) {
		this.component1 = component1;
	}

	public int getComponent1() {
		return component1;
	}

	public void setMolecule2(int molecule2) {
		this.molecule2 = molecule2;
	}

	public int getMolecule2() {
		return molecule2;
	}

	public void setState1(int state1)
	{
		this.state1 = state1;	
	}

	public int getState1() {
		return state1;
	}

	public void setState2(int state2) {
		this.state2 = state2;
	}

	public int getState2() {
		return state2;
	}

	public void setComponent2(int component2) {
		this.component2 = component2;
	}

	public int getComponent2() {
		return component2;
	}
	
	public boolean equals(Bond in)
	{
		// Check in one direction
		if(this.getMolecule1() == in.getMolecule1() 
				&& this.getMolecule2() == in.getMolecule2() 
				&& this.getComponent1() == in.getComponent1() 
				&& this.getComponent2() == in.getComponent2() 
				&& this.getState1() == in.getState1() 
				&& this.getState2() == in.getState2())
		{
			return true;
		}
		
		// Check in the other direction
		else if(this.getMolecule1() == in.getMolecule2() 
				&& this.getMolecule2() == in.getMolecule1()
				&& this.getComponent1() == in.getComponent2()
				&& this.getComponent2() == in.getComponent1()
				&& this.getState1() == in.getState2()
				&& this.getState2() == in.getState1())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
