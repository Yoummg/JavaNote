### 贪婪法

这种方法模式一般将求解过程分成若干个步骤，在每个步骤都应用贪心原则，选取当前状态下最好的或者最优的选择（局部最有利的选择），并以此希望最后堆叠出的结果也是最好或者最优的解。

大多数情况下由于策略的 `“短视”`，贪婪法会错过真正的最优解，得不到问题的真正答案。但是贪婪法简单有效，省去了为找最优解可能需要的穷举操作，可以得到与最优解比较接近的近似最优解，通常作为其他算法的辅助算法使用。

贪婪法的基本设计思想有以下三个步骤：

1. 建立对问题精确描述的数学模型，包括定义最优解的模型；

2. 将问题分解为一系列子问题，同时定义子问题的最优解结构；

3. 应用贪心原则确定每个子问题的局部最优解，并根据最优解模型，用子问题的局部最优解
         堆叠出全局最优解。

>
举个经典的例子：找零钱。假如某国发行的货币有25分、10分、5分和1分四种硬币，假如你是售货员，找给顾客41分钱的硬币，如何安排能使找给客人的钱正确，但是硬币个数最少。
>
解答：选择的策略是贪婪策略，即在币值总和不超过41的前提下选择币值最大的那种硬币。(1)25,(2)10,3(5),4(1)。

**0-1背包问题（knapscak problem）**：有N件物品和一种承重为C的背包，每个物品的重量是wi，价值是pi，求解将哪几件物品装入背包可使这些物品在总和不超过C的情况下价值总和最大。


一个具体实例，有一个背包，最多能承载重量为C=150的物品，现在有七个物品（物品不能分割成任意大小），编号为1~7，
重量分别为wi=[35,30,60,50,40,10,25],价值分别是pi=[10,40,30,50,35,40,30]现在从这7个物品中选择一个或多个装入背包，要求在物
品总重量不超过C的前提下，所装入的物品总价值最高。

- 第一种策略价值选择：编号4,2,6,5 重量 130 价值165

- 第二种策略每次选择最轻：编号6,7,2,1,5 重量 140 价值 155

- 第三种策略价值密度（si=pi/wi）：编号6,2,7,4,1 重量150 价值 170

```java
public class KnapsackProblem {

  private class TagObject {
    //默认生成属性的get/set方法
    public int weight;
    public int price;
    public int status = 0;//0：未选中；1：已选中；2：已经不可选
  }

  private static class TagKnapsackProblem {
    //objs 是初始化好的数值
    //重量分别为wi=[35,30,60,50,40,10,25],
    //价值分别是pi=[10,40,30,50,35,40,30],
    public Vector<TagObject> objs;
    public int totalC;
  }

  /*
    greedyAlgo()方法是贪婪算法的主体结构，包括子问题的分解和选择策略的选择都在这个方法中。
    正如方法所展示的那样，它可以作为此类为题的一个通用的解决思路。
  */
  public void greedyAlga(SELECT_POLICY spFunc) {
    int idx;
    int ntc = 0;

    //spFunc 每次选最符合策略的那件物品，然后在检查
    while ((idx = spFunc()) != -1) {
      //所选物品是否满足背包承重要求？
      if (TagKnapsackProblem.objs.get(idx).getWeight() <= TagKnapsackProblem.totalC) {

        TagKnapsackProblem.objs.get(idx).setStatus() = 1;
        ntc += TagKnapsackProblem.objs.get(idx).getWeight();

      } else {
        TagKnapsackProblem.objs.get(idx).setStatus() = 2;
      }

    }
    //形式化输出，实际的输出是：for循环 + status为1;
    System.out.println(TagKnapsackProblem.objs);

  }

  /*
    SpFunc参数是选择策略函数的接口，通过替换这个参数，可以实现上文所提到的三种贪婪策略，
    分别得到各种贪婪策略下得到的解。以第一种为例，可以这样实现：
  */
  public int chooseFunc1() {
    int index = -1;
    int tmp = 0;

    for (int i = 0; i < TagKnapsackProblem.objs.size(); i++) {

      if ((TagKnapsackProblem.objs.get(i).getStatus() == 0) &&
          (TagKnapsackProblem.objs.get(i).getPrice() > tmp)) {

        tmp = TagKnapsackProblem.objs.get(i).getPrice();
        index = i;

      }

    }
    return index;
  }
  ```