package test;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Akang
 * @create 2022-12-19 15:43
 */
public class BeanCopyTest {
    private int age ;
    private String favoriteFruit ;
    private int weight ;
    private String favoriteBall ;
    private String sex ;
    private String favoriteComputer ;
    private String name ;

    public void setAge(int age) {
        this.age = age;
    }

    public void setFavoriteFruit(String favoriteFruit) {
        this.favoriteFruit = favoriteFruit;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setFavoriteBall(String favoriteBall) {
        this.favoriteBall = favoriteBall;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setFavoriteComputer(String favoriteComputer) {
        this.favoriteComputer = favoriteComputer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getFavoriteFruit() {
        return favoriteFruit;
    }

    public int getWeight() {
        return weight;
    }

    public String getFavoriteBall() {
        return favoriteBall;
    }

    public String getSex() {
        return sex;
    }

    public String getFavoriteComputer() {
        return favoriteComputer;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BeanCopyTest{" +
                "age=" + age +
                ", favoriteFruit='" + favoriteFruit + '\'' +
                ", weight=" + weight +
                ", favoriteBall='" + favoriteBall + '\'' +
                ", sex='" + sex + '\'' +
                ", favoriteComputer='" + favoriteComputer + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static void main(String[] args) throws Exception {
        Properties p = new Properties("hadoop","男",26,73);
        Loves o  = new Loves("banana","football","mac") ;
        BeanCopyTest bean = new BeanCopyTest() ;
        BeanUtils.copyProperties(p,bean);
        BeanUtils.copyProperties(o,bean);
        System.out.println(bean.toString());
    }
}
class Properties{
    private String name ;
    private String sex ;
    private int age ;
    private int weight ;

    public Properties(String name, String sex, int age, int weight) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

class Loves{
    private String favoriteFruit ;
    private String favoriteBall ;
    private String favoriteComputer ;

    public Loves(String favoriteFruit, String favoriteBall, String favoriteComputer) {
        this.favoriteFruit = favoriteFruit;
        this.favoriteBall = favoriteBall;
        this.favoriteComputer = favoriteComputer;
    }

    public void setFavoriteFruit(String favoriteFruit) {
        this.favoriteFruit = favoriteFruit;
    }

    public void setFavoriteBall(String favoriteBall) {
        this.favoriteBall = favoriteBall;
    }

    public void setFavoriteComputer(String favoriteComputer) {
        this.favoriteComputer = favoriteComputer;
    }

    public String getFavoriteFruit() {
        return favoriteFruit;
    }

    public String getFavoriteBall() {
        return favoriteBall;
    }

    public String getFavoriteComputer() {
        return favoriteComputer;
    }
}
