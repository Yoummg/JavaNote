### 问题：汉诺塔问题（把柱1的全部移动到柱3）

解决问题的步骤：

1.先上面的n-1个圆盘从柱1到柱2；

2.再把柱1上剩下的一个圆盘移动到柱3；

3.最后把柱2上的n-1个圆盘移动到柱3。


分析汉诺塔递归算法的时间代价，来统计所有圆盘移动的总次数。
设n个圆盘的塔必须移动的总次数为moves(n)

则有：

        moves(n) = 1                if n==1

        moves(n) = 1+2moves(n-1)    if n>1

这个递归方程的解是 moves(n) = pow(2,n)-1 因此时间代价为 O(pow(2,n)))

```java
public class hannuota(){

  void move(int n,int source,int dest){

    if(n==0) return;
    int spare = 6 -source - dest;
    move(n-1,source,spare);
    System.out.println("Move disk from "+ source +" to " +dest);
    move(n-1,spare,dest);

  }

}

```