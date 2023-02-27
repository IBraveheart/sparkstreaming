package test;

import com.alibaba.fastjson.JSON;

/**
 * @author Akang
 * @create 2022-12-16 14:45
 */
public class JsonTest {
    private int height ;
    private String name ;
    private int age ;
    private int weight ;
    private String gender ;
    private String city ;

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public String getGender() {
        return gender;
    }

    public String getCity() {
        return city;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "JsonTest{" +
                "height=" + height +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public static void main(String[] args) {
        String jsonStr ="{\"city\":\"changsha\",\"sex\":\"男\",\"gender\":26,\"name\":\"tom\",\"weight\":78,\"height\":178}" ;
        JsonTest jsonTest = JSON.parseObject(jsonStr, JsonTest.class);
        System.out.println(jsonTest);
    }
}
