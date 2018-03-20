
public class Test {

	
	public static void main(String[] args) {
		System.out.println("This is a  test! ");
	}
	
	
	public int[] twoSum(int[] nums, int target) {
        for(int i = 0; i < nums.length; i ++) {
            for(int j = 0; j < nums.length; j++) {
               if(nums[i] + nums[j] == target) {
                   return new int[]{i,j};
               }
            }
        }
        return null;
    }
}
