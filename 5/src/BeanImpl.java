import java.util.Random;


public class BeanImpl implements Bean {

	// TODO: Add more member variables as needed
	private int xpos;
	private boolean isLuck;
	private int slotCount;
	private Random rand;
	private double skillAvg;
	private int skillLevel;
	private int backupSkill;
	private double skillStdev;

	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param slotCount the number of slots in the machine
	 * @param isLuck    whether the bean is in luck mode
	 * @param rand      the random number generator
	 */
	BeanImpl(int slotCount, boolean isLuck, Random rand) {
		// TODO: Implement
		this.slotCount = slotCount;
		this.isLuck = isLuck;
		this.rand = rand;
		skillAvg = (slotCount - 1) * 0.5;
		skillStdev = Math.sqrt(slotCount * 0.5 * 0.5);
		skillLevel = (int) Math.round(rand.nextGaussian() * skillStdev + skillAvg);
		backupSkill = skillLevel;
	}

	/**
	 * Returns the current X-coordinate position of the bean in the logical
	 * coordinate system.
	 * 
	 * @return the current X-coordinate of the bean
	 */
	public int getXPos() {
		// TODO: Implement
		return xpos;
	}

	/**
	 * Resets the bean to its initial state. The X-coordinate should be initialized
	 * to 0.
	 */
	public void reset() {
		// TODO: Implement
		xpos = 0;
		skillLevel = backupSkill;
	}

	/**
	 * Chooses left or right randomly (if luck) or according to skill. If the return
	 * value of rand.nextInt(2) is 0, the bean goes left. Otherwise, the bean goes
	 * right. The X-coordinate is updated accordingly.
	 */
	public void choose() {
		// TODO: Implement
		if (isLuck) {
			int dir = rand.nextInt(2);

			if (dir == 0) {
                //this is a comment
			} else if (dir == 1) {
				if (xpos < slotCount - 1) {
					xpos++;
				}
			}
		} else {
			if (skillLevel == 0) {
				//this is a comment
			} else if (skillLevel == 9) {
				if (xpos < slotCount - 1) {
					xpos++;
				}
			} else {
				skillLevel--;
				if (xpos < slotCount - 1) {
					xpos++;
				}
			}
		}
	}
}