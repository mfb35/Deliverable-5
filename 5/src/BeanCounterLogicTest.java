import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.nasa.jpf.vm.Verify;
import java.util.Random;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class BeanCounterLogicTest {
	private static BeanCounterLogic logic; // The core logic of the program
	private static Bean[] beans; // The beans in the machine
	private static String failString; // A descriptive fail string for assertions

	private static int slotCount; // The number of slots in the machine we want to test
	private static int beanCount; // The number of beans in the machine we want to test
	private static boolean isLuck; // Whether the machine we want to test is in "luck" or "skill" mode

	/**
	 * Sets up the test fixture.
	 */
	@BeforeClass
	public static void setUp() {
		if (Config.getTestType() == TestType.JUNIT) {

			slotCount = 5;
			beanCount = 0;
			isLuck = true;
		} else if (Config.getTestType() == TestType.JPF_ON_JUNIT) {
			/*
			 * TODO: Use the Java Path Finder Verify API to generate choices for slotCount,
			 * beanCount, and isLuck: slotCount should take values 1-5, beanCount should
			 * take values 0-3, and isLucky should be either true or false. For reference on
			 * how to use the Verify API, look at:
			 * https://github.com/javapathfinder/jpf-core/wiki/Verify-API-of-JPF
			 */
			slotCount = Verify.getInt(1, 5);
			beanCount = Verify.getInt(0, 3);
			isLuck = Verify.getBoolean();
		} else {
			assert (false);
		}

		// Create the internal logic
		logic = BeanCounterLogic.createInstance(slotCount);
		// Create the beans
		beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(slotCount, isLuck, new Random(42));
		}

		// A failstring useful to pass to assertions to get a more descriptive error.
		failString = "Failure in (slotCount=" + slotCount + ", beanCount="
				+ beanCount + ", isLucky=" + isLuck + "):";
	}

	@AfterClass
	public static void tearDown() {
	}

	/**
	 * Test case for void reset(Bean[] beans). Preconditions: None. Execution steps:
	 * Call logic.reset(beans). Invariants: If beanCount is greater than 0,
	 * remaining bean count is beanCount - 1 in-flight bean count is 1 (the bean
	 * initially at the top) in-slot bean count is 0. If beanCount is 0, remaining
	 * bean count is 0 in-flight bean count is 0 in-slot bean count is 0.
	 */
	@Test
	public void testReset() {
		// TODO: Implement
		/*
		 * Currently, it just prints out the failString to demonstrate to you all the
		 * cases considered by Java Path Finder. If you called the Verify API correctly
		 * in setUp(), you should see all combinations of machines
		 * (slotCount/beanCount/isLucky) printed here:
		 * 
		 * Failure in (slotCount=1, beanCount=0, isLucky=false): Failure in
		 * (slotCount=1, beanCount=0, isLucky=true): Failure in (slotCount=1,
		 * beanCount=1, isLucky=false): Failure in (slotCount=1, beanCount=1,
		 * isLucky=true): ...
		 * 
		 * PLEASE REMOVE when you are done implementing.
		 */
		logic.reset(beans);

		int beansAtSlot = 0;
		int beansInFlight = 0;

		for (int i = 0; i < slotCount; i++) {
			if (logic.getInFlightBeanXPos(i) != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				beansInFlight++;
			}

			beansAtSlot += logic.getSlotBeanCount(i);
		}
		if (beanCount > 0) {

			assertEquals(beanCount - 1, logic.getRemainingBeanCount());
			assertEquals(0, beansAtSlot);
			assertEquals(1, beansInFlight);
		} else if (beanCount == 0) {
			assertEquals(0, logic.getRemainingBeanCount());
			assertEquals(0, beansAtSlot);
			assertEquals(0, beansInFlight);
		}
		// System.out.println(failString);
	}

	/**
	 * Test case for boolean advanceStep(). Preconditions: None. Execution steps:
	 * Call logic.reset(beans). Call logic.advanceStep() in a loop until it returns
	 * false (the machine terminates). Invariants: After each advanceStep(), all
	 * positions of in-flight beans are legal positions in the logical coordinate
	 * system.
	 */
	@Test
	public void testAdvanceStepCoordinates() {
		// TODO: Implement
		logic.reset(beans);

		while (true) {
			boolean response = logic.advanceStep();

			for (int i = 0; i < slotCount; i++) {
				int xpos = logic.getInFlightBeanXPos(i);
				if (xpos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
					assertTrue(xpos >= 0 && xpos <= i);
				}
			}
			if (response == false) {
				break;
			}
		}
	}

	/**
	 * Test case for boolean advanceStep(). Preconditions: None. Execution steps:
	 * Call logic.reset(beans). Call logic.advanceStep() in a loop until it returns
	 * false (the machine terminates). Invariants: After each advanceStep(), the sum
	 * of remaining, in-flight, and in-slot beans is equal to beanCount.
	 */
	@Test
	public void testAdvanceStepBeanCount() {
		// TODO: Implement
		logic.reset(beans);

		while (true) {
			int beansInAction = 0;
			int beansInSlot = 0;
			int remainingBeans = 0;
			
			for (int i = 0; i < slotCount; i++) {
				int xpos = logic.getInFlightBeanXPos(i);
				if (xpos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
					beansInAction++;
				}
				beansInSlot += logic.getSlotBeanCount(i);
			}
			
			boolean response = logic.advanceStep();
			remainingBeans = logic.getRemainingBeanCount();
			assertTrue(beanCount == (beansInSlot + beansInAction + remainingBeans));
			if (response == false) {
				break;
			}
		}

	}

	/**
	 * Test case for boolean advanceStep(). Preconditions: None. Execution steps:
	 * Call logic.reset(beans). Call logic.advanceStep() in a loop until it returns
	 * false (the machine terminates). Invariants: After the machine terminates,
	 * remaining bean count is 0 in-flight bean count is 0 in-slot bean count is
	 * beanCount.
	 */
	@Test
	public void testAdvanceStepPostCondition() {
		// TODO: Implement
		logic.reset(beans);
		int beansInFlight = 0;
		int beanSlotCount = 0;
		while (true) {
			boolean response = logic.advanceStep();
			if (response == false) {
				break;
			}
		}

		for (int i = 0; i < slotCount; i++) {
			int xPos = logic.getInFlightBeanXPos(i);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				beansInFlight++;
			}
			beanSlotCount += logic.getSlotBeanCount(i);
		}
		assertEquals(0, logic.getRemainingBeanCount());
		assertTrue(beansInFlight == 0);
		assertEquals(beanSlotCount, beanCount);
	}

	/**
	 * Test case for void lowerHalf()(). Preconditions: None. Execution steps: Call
	 * logic.reset(beans). Call logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.lowerHalf(). Invariants: After the
	 * machine terminates, remaining bean count is 0 in-flight bean count is 0
	 * in-slot bean count is beanCount. After calling logic.lowerHalf(), slots in
	 * the machine contain only the lower half of the original beans. Remember, if
	 * there were an odd number of beans, (N+1)/2 beans should remain. Check each
	 * slot for the expected number of beans after having called logic.lowerHalf().
	 */
	@Test
	public void testLowerHalf() {

		logic.reset(beans);
		int beansInFlight = 0;
		int beanSlotCount = 0;
		int lowerHalf = -1;
		if (beanCount % 2 != 0) {
			lowerHalf = (beanCount + 1) / 2;
		} else {
			lowerHalf = beanCount / 2;
		}
		int[] lHalf = new int[slotCount];

		while (true) {
			boolean response = logic.advanceStep();
			if (response == false) {
				break;
			}
		}

		for (int i = 0; i < slotCount; i++) {
			int xPos = logic.getInFlightBeanXPos(i);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				beansInFlight++;
			}
			beanSlotCount += logic.getSlotBeanCount(i);
			lHalf[i] = logic.getSlotBeanCount(i);
		}

		assertTrue(logic.getRemainingBeanCount() == 0);
		assertTrue(beansInFlight == 0);
		assertTrue(beanSlotCount == beanCount);

		logic.lowerHalf();
		beanSlotCount = 0;
		for (int i = 0; i < slotCount; i++) {
			beanSlotCount += logic.getSlotBeanCount(i);
		}
		// System.out.println("beanSlotCount: " + beanSlotCount + " lowerHalf: " +
		// lowerHalf);
		assertEquals(beanSlotCount, lowerHalf);

		// remove half beans starting from the highest slot and make sure they are the
		// same
		// as the logic bean slot counts
		int counter = beanCount - lowerHalf;
		while (counter > 0) {
			int slot = -1;
			for (int i = (lHalf.length - 1); i >= 0; i--) {
				if (lHalf[i] != 0) {
					slot = i;
					break;
				}
			}

			if (slot == -1) {
				break;
			}

			counter--;
			lHalf[slot]--;

		}

		for (int i = lHalf.length - 1; i >= 0; i--) {
			assertTrue(lHalf[i] == logic.getSlotBeanCount(i));
		}
	}

	/**
	 * Test case for void upperHalf(). Preconditions: None. Execution steps: Call
	 * logic.reset(beans). Call logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.lowerHalf(). Invariants: After the
	 * machine terminates, remaining bean count is 0 in-flight bean count is 0
	 * in-slot bean count is beanCount. After calling logic.upperHalf(), slots in
	 * the machine contain only the upper half of the original beans. Remember, if
	 * there were an odd number of beans, (N+1)/2 beans should remain. Check each
	 * slot for the expected number of beans after having called logic.upperHalf().
	 */
	@Test
	public void testUpperHalf() {
		// TODO: Implement
		// need to figure out how to get expected beans in each slot.
		logic.reset(beans);
		int beansInFlight = 0;
		int beanSlotCount = 0;
		int upperHalf = -1;

		if (beanCount % 2 != 0) {
			upperHalf = (beanCount + 1) / 2;
		} else {
			upperHalf = beanCount / 2;
		}
		int[] uHalf = new int[slotCount];

		while (true) {
			boolean response = logic.advanceStep();
			if (response == false) {
				break;
			}
		}

		for (int i = 0; i < slotCount; i++) {
			int xPos = logic.getInFlightBeanXPos(i);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				beansInFlight++;
			}
			beanSlotCount += logic.getSlotBeanCount(i);
			// System.out.print(logic.getSlotBeanCount(i) + " ");
			uHalf[i] = logic.getSlotBeanCount(i);
			// System.out.print(uHalf[i] + " ");
		}
		// System.out.println();
		assertTrue(logic.getRemainingBeanCount() == 0);
		assertTrue(beansInFlight == 0);
		assertTrue(beanSlotCount == beanCount);

		logic.upperHalf();
		beanSlotCount = 0;

		for (int i = 0; i < slotCount; i++) {
			// System.out.print(logic.getSlotBeanCount(i) + " ");
			beanSlotCount += logic.getSlotBeanCount(i);
		}

		assertEquals(beanSlotCount, upperHalf);

		// remove half beans starting from the highest slot and make sure they are the
		// same
		// as the logic bean slot counts
		int counter = beanCount - upperHalf;
		while (counter > 0) {
			int slot = -1;
			for (int i = 0; i < uHalf.length; i++) {
				if (uHalf[i] != 0) {
					slot = i;
					break;
				}
			}

			if (slot == -1) {
				break;
			}
			uHalf[slot] -= 1;
			counter -= 1;
		}

		for (int i = 0; i < uHalf.length; i++) {
			// System.out.print(uHalf[i] + " ");
			assertTrue(uHalf[i] == logic.getSlotBeanCount(i));
		}
	}

	/**
	 * Test case for void repeat(). Preconditions: None. Execution steps: Call
	 * logic.reset(beans). Call logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.repeat(); Call logic.advanceStep() in a
	 * loop until it returns false (the machine terminates). Invariants: If the
	 * machine is operating in skill mode, bean count in each slot is identical
	 * after the first run and second run of the machine.
	 */
	@Test
	public void testRepeat() {
		// TODO: Implement
		// need to figure out how to get expected beans in each slot.
		logic.reset(beans);
		int[] prearr = new int[slotCount];

		while (true) {
			boolean response = logic.advanceStep();
			if (response == false) {
				break;
			}
		}

		for (int i = 0; i < slotCount; i++) {
			prearr[i] = logic.getSlotBeanCount(i);
		}

		logic.repeat();

		while (true) {
			boolean response = logic.advanceStep();
			if (response == false) {
				break;
			}
		}

		if (isLuck == false) {
			for (int i = 0; i < slotCount; i++) {
				assertTrue(prearr[i] == logic.getSlotBeanCount(i));
			}
		}
	}
}
