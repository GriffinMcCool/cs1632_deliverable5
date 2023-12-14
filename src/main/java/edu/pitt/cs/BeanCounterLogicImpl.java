package edu.pitt.cs;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Code by @author Wonsun Ahn.  Copyright Fall 2022.
 * 
 * <p>
 * BeanCounterLogic: The bean counter, also known as a quincunx or the Galton
 * box, is a device for statistics experiments named after English scientist Sir
 * Francis Galton. It consists of an upright board with evenly spaced nails (or
 * pegs) in a triangular form. Each bean takes a random path and falls into a
 * slot.
 *
 * <p>
 * Beans are dropped from the opening of the board. Every time a bean hits a
 * nail, it has a 50% chance of falling to the left or to the right. The piles
 * of beans are accumulated in the slots at the bottom of the board.
 * 
 * <p>
 * This class implements the core logic of the machine. The MainPanel uses the
 * state inside BeanCounterLogic to display on the screen.
 * 
 * <p>
 * Note that BeanCounterLogic uses a logical coordinate system to store the
 * positions of in-flight beans.For example, for a 4-slot machine:
 * 
 * <pre>
 *                      (0, 0)
 *               (0, 1)        (1, 1)
 *        (0, 2)        (1, 2)        (2, 2)
 *  (0, 3)       (1, 3)        (2, 3)       (3, 3)
 * [Slot0]       [Slot1]       [Slot2]      [Slot3]
 * </pre>
 */

public class BeanCounterLogicImpl implements BeanCounterLogic {
	// TODO: Add member methods and variables as needed
	private int slotCount;
	private Bean[] beans;
	private Queue<Bean> waitingBeans;
	private Bean[] inFlightBeans;
	private LinkedList<Bean>[] slots;


	/**
	 * Constructor - creates the bean counter logic object that implements the core
	 * logic with the provided number of slots.
	 * 
	 * @param slotCount the number of slots in the machine
	 */
	BeanCounterLogicImpl(int slotCount) {
		// TODO: Implement
		this.slotCount = slotCount;
	}

	/**
	 * Returns the number of slots the machine was initialized with.
	 * 
	 * @return number of slots
	 */
	public int getSlotCount() {
		// TODO: Implement
		return slotCount;
	}
	
	/**
	 * Returns the number of beans remaining that are waiting to get inserted.
	 * 
	 * @return number of beans remaining
	 */
	public int getRemainingBeanCount() {
		// TODO: Implement
		return waitingBeans.size();
	}

	/**
	 * Returns the x-coordinate for the in-flight bean at the provided y-coordinate.
	 * 
	 * @param yPos the y-coordinate in which to look for the in-flight bean
	 * @return the x-coordinate of the in-flight bean; if no bean in y-coordinate, return NO_BEAN_IN_YPOS
	 */
	public int getInFlightBeanXPos(int yPos) {
		// TODO: Implement
		if (inFlightBeans[yPos] == null) {
			return NO_BEAN_IN_YPOS;
		} else {
			return inFlightBeans[yPos].getXPos();
		}
		
	}

	/**
	 * Returns the number of beans in the ith slot.
	 * 
	 * @param i index of slot
	 * @return number of beans in slot
	 */
	public int getSlotBeanCount(int i) {
		// TODO: Implement
		return slots[i].size();
	}

	/**
	 * Calculates the average slot number of all the beans in slots.
	 * 
	 * @return Average slot number of all the beans in slots.
	 */
	public double getAverageSlotBeanCount() {
		// TODO: Implement
		double sum = 0;
		double total = 0;
		for (int i = 0; i < slotCount; i++) {
			int beanCount = slots[i].size();
			sum += (double) beanCount * i;
			total += (double) beanCount;
		}

		if (total > 0) {
			return sum / total;
		} else {
			return 0.0;
		}
		
	}

	/**
	 * Removes the lower half of all beans currently in slots, keeping only the
	 * upper half. If there are an odd number of beans, remove (N-1)/2 beans, where
	 * N is the number of beans. So, if there are 3 beans, 1 will be removed and 2
	 * will be remaining.
	 */
	public void upperHalf() {
		// TODO: Implement

		//get total number of beans in slots
		int total = 0;
		for (int i = 0; i < slotCount; i++) {
			total += slots[i].size();
		}

		//figure out how many beans to remove
		int beansToRemove = 0;
		if (total % 2 == 0) {
			beansToRemove = total / 2;
		} else {
			beansToRemove = (total - 1) / 2;
		}

		//remove lower half of beans
		for (int i = 0; i < slots.length && beansToRemove > 0; i++) {
			int count = slots[i].size();
			if (beansToRemove > count) {
				slots[i].clear();
				beansToRemove -= count;
			} else {
				while (beansToRemove > 0) {
					slots[i].pop();
					beansToRemove--;
				}
			}
		}
	}

	/**
	 * Removes the upper half of all beans currently in slots, keeping only the
	 * lower half.  If there are an odd number of beans, remove (N-1)/2 beans, where
	 * N is the number of beans. So, if there are 3 beans, 1 will be removed and 2
	 * will be remaining.
	 */
	public void lowerHalf() {

		//get total number of beans in slots
		int total = 0;
		for (int i = 0; i < slotCount; i++) {
			total += slots[i].size();
		}

		//figure out how many beans to remove
		int beansToRemove = 0;
		if (total % 2 == 0) {
			beansToRemove = total / 2;
		} else {
			beansToRemove = (total - 1) / 2;
		}
		//remove upper half of beans
		for (int i = slots.length - 1; i >= 0 && beansToRemove > 0; i--) {
			int count = slots[i].size();
			if (beansToRemove > count) {
				slots[i].clear();
				beansToRemove -= count;
			} else {
				while (beansToRemove > 0) {
					slots[i].pop();
					beansToRemove--;
				}
			}
		}
	}

	/**
	 * A hard reset. Initializes the machine with the passed beans. The machine
	 * starts with one bean at the top.
	 * 
	 * @param beans array of beans to add to the machine
	 */
	public void reset(Bean[] beans) {
		// TODO: Implement
		waitingBeans = new LinkedList<Bean>();
		inFlightBeans = new Bean[slotCount];
		slots = new LinkedList[slotCount];

		//fills the inFlight array with nulls
		for (int i = 0; i < inFlightBeans.length; i++) {
			inFlightBeans[i] = null;
		}

		//fills slots array with 0s
		for (int i = 0; i < slotCount; i++) {
			slots[i] = new LinkedList<Bean>();
		}
		if (beans.length > 0) {
			//initializes bean data structures
			Bean[] beans2 = new Bean[beans.length];
			for (int i = 0; i < beans.length; i++) {
				beans2[i] = beans[i];
			}
			this.beans = beans2;
			//adds all beans to the waiting queue
			for (int i = 0; i < beans.length; i++) {
				this.beans[i].reset();
				waitingBeans.add(this.beans[i]);
			}

			//sets the first bean in flight
			inFlightBeans[0] = waitingBeans.remove();
		}
	}

	/**
	 * Repeats the experiment by scooping up all beans in the slots and all beans
	 * in-flight and adding them into the pool of remaining beans. As in the
	 * beginning, the machine starts with one bean at the top.
	 */
	public void repeat() {
		// TODO: Implement

		//add all beans from slots to queue
		for (int i = 0; i < slots.length; i++) {
			while (slots[i].size() > 0) {
				Bean bean = slots[i].pop();
				bean.reset();
				waitingBeans.add(bean);
			}
		}

		//add all beans in flight to the queue
		for (int i = 0; i < inFlightBeans.length; i++) {
			if (inFlightBeans[i] != null) {
				inFlightBeans[i].reset();
				waitingBeans.add(inFlightBeans[i]);
				inFlightBeans[i] = null;
			}
		}
		

		if (!waitingBeans.isEmpty()) {
			inFlightBeans[0] = waitingBeans.remove();
		}
		
	}

	/**
	 * Advances the machine one step. All the in-flight beans fall down one step to
	 * the next peg. A new bean is inserted into the top of the machine if there are
	 * beans remaining.
	 * 
	 * @return whether there has been any status change. If there is no change, that
	 *         means the machine is finished.
	 */
	public boolean advanceStep() {
		// TODO: Implement
		boolean change = false;

		//cycle through inFlight array and advance any beans in flight
		for (int i = inFlightBeans.length - 1; i >= 0; i--) {
			if (inFlightBeans[i] != null) {
				Bean bean = inFlightBeans[i];
				if (i == inFlightBeans.length - 1) {
					slots[bean.getXPos()].add(bean);
					inFlightBeans[i] = null;
				} else {
					bean.advanceStep();
					inFlightBeans[i + 1] = bean;
					inFlightBeans[i] = null;
				}
				change = true;
			}
		}

		//get next waiting bean from queue
		if (waitingBeans.size() > 0) {
			inFlightBeans[0] = waitingBeans.remove();
			change = true;
		}
		


		return change;
	}
	
	/**
	 * Number of spaces in between numbers when printing out the state of the machine.
	 * Make sure the number is odd (even numbers don't work as well).
	 */
	private int xspacing = 3;

	/**
	 * Calculates the number of spaces to indent for the given row of pegs.
	 * 
	 * @param yPos the y-position (or row number) of the pegs
	 * @return the number of spaces to indent
	 */
	private int getIndent(int yPos) {
		int rootIndent = (getSlotCount() - 1) * (xspacing + 1) / 2 + (xspacing + 1);
		return rootIndent - (xspacing + 1) / 2 * yPos;
	}

	/**
	 * Constructs a string representation of the bean count of all the slots.
	 * 
	 * @return a string with bean counts for each slot
	 */
	public String getSlotString() {
		StringBuilder bld = new StringBuilder();
		Formatter fmt = new Formatter(bld);
		String format = "%" + (xspacing + 1) + "d";
		for (int i = 0; i < getSlotCount(); i++) {
			fmt.format(format, getSlotBeanCount(i));
		}
		fmt.close();
		return bld.toString();
	}

	/**
	 * Constructs a string representation of the entire machine. If a peg has a bean
	 * above it, it is represented as a "1", otherwise it is represented as a "0".
	 * At the very bottom is attached the slots with the bean counts.
	 * 
	 * @return the string representation of the machine
	 */
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
		    value = "VA_FORMAT_STRING_USES_NEWLINE", 
		    justification = "I know we should be using %n instead of \n, but JPF for some reason does not like %n")
	public String toString() {
		StringBuilder bld = new StringBuilder();
		Formatter fmt = new Formatter(bld);
		for (int yPos = 0; yPos < getSlotCount(); yPos++) {
			int xBeanPos = getInFlightBeanXPos(yPos);
			for (int xPos = 0; xPos <= yPos; xPos++) {
				int spacing = (xPos == 0) ? getIndent(yPos) : (xspacing + 1);
				String format = "%" + spacing + "d";
				if (xPos == xBeanPos) {
					fmt.format(format, 1);
				} else {
					fmt.format(format, 0);
				}
			}
			fmt.format("\n");
		}
		fmt.close();
		return bld.toString() + getSlotString();
	}

	/**
	 * Prints usage information.
	 */
	public static void showUsage() {
		System.out.println("Usage: java BeanCounterLogic slot_count bean_count <luck | skill> [debug]");
		System.out.println("Example: java BeanCounterLogic 10 400 luck");
		System.out.println("Example: java BeanCounterLogic 20 1000 skill debug");
	}
	
	/**
	 * Auxiliary main method. Runs the machine in text mode with no bells and
	 * whistles. It simply shows the slot bean count at the end.
	 * 
	 * @param args commandline arguments; see showUsage() for detailed information
	 */
	public static void main(String[] args) {
		boolean debug;
		boolean luck;
		int slotCount = 0;
		int beanCount = 0;

		if (args.length != 3 && args.length != 4) {
			showUsage();
			return;
		}

		try {
			slotCount = Integer.parseInt(args[0]);
			beanCount = Integer.parseInt(args[1]);
		} catch (NumberFormatException ne) {
			showUsage();
			return;
		}
		if (beanCount < 0) {
			showUsage();
			return;
		}

		if (args[2].equals("luck")) {
			luck = true;
		} else if (args[2].equals("skill")) {
			luck = false;
		} else {
			showUsage();
			return;
		}
		
		if (args.length == 4 && args[3].equals("debug")) {
			debug = true;
		} else {
			debug = false;
		}

		// Create the internal logic
		BeanCounterLogicImpl logic = new BeanCounterLogicImpl(slotCount);
		// Create the beans (in luck mode)
		BeanImpl[] beans = new BeanImpl[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = new BeanImpl(slotCount, luck, new Random());
		}
		// Initialize the logic with the beans
		logic.reset(beans);

		if (debug) {
			System.out.println(logic.toString());
		}

		// Perform the experiment
		while (true) {
			if (!logic.advanceStep()) {
				break;
			}
			if (debug) {
				System.out.println(logic.toString());
			}
		}
		// display experimental results
		System.out.println("Slot bean counts:");
		System.out.println(logic.getSlotString());
	}
}
