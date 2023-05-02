package top.rrricardo.postcalendarbackend.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

@SpringBootTest
public class AvlTreeTest {

    @Test
    void TestInsert1() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        var actual = tree.toString();
        Assert.isTrue(actual.equals("1 "), actual);
    }

    @Test
    void TestInsert2() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        tree.insert(4);
        tree.insert(5);

        var actual = tree.toString();
        var expect = "1 2 3 4 5 ";
        Assert.isTrue(actual.equals(expect), actual);
    }

    @Test
    void TestInsert3() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(5);
        tree.insert(4);
        tree.insert(3);
        tree.insert(2);
        tree.insert(1);

        var actual = tree.toString();
        var expect = "1 2 3 4 5 ";
        Assert.isTrue(actual.equals(expect), actual);
    }

    @Test
    void TestInsert4() throws AvlNodeRepeatException {
        // 严格来说
        // 这个单元测试是有问题的
        // 输入的数据有可能重复
        var random = new Random();
        var length = 1000;
        var testInput = new int[length];
        var tree = new AvlTree<Integer>();

        for(var i = 0; i < length; i++) {
            var input = random.nextInt();
            testInput[i] = input;
            tree.insert(input);
        }

        Arrays.sort(testInput);
        var builder = new StringBuilder();
        for(var i = 0; i < length; i++) {
            builder.append(testInput[i]).append(' ');
        }

        var expect = builder.toString();
        var actual = tree.toString();
        Assert.isTrue(expect.equals(actual), actual);
    }

    @Test
    void TestRemove1() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        tree.remove(1);

        var actual = tree.toString();

        Assert.isTrue(actual.equals(""), actual);
    }

    @Test
    void TestRemove2() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        tree.insert(4);
        tree.insert(5);

        tree.remove(2);
        tree.remove(4);
        tree.remove(6);

        var actual = tree.toString();
        Assert.isTrue(actual.equals("1 3 5 "), actual);
    }

    @Test
    void TestRemove3() throws AvlNodeRepeatException {
        // 严格来说
        // 这个单元测试是有问题的
        // 输入的数据有可能重复
        var random = new Random();
        var length = 1000;
        var testInput = new ArrayList<Integer>();
        var tree = new AvlTree<Integer>();

        for (var i = 0; i < length; i++) {
            var input = random.nextInt();
            testInput.add(input);
            tree.insert(input);
        }

        for (var i = length - 1; i > length / 2; i--) {
            var input = testInput.get(i);
            testInput.remove(input);
            tree.remove(input);
        }

        testInput.sort(Comparator.naturalOrder());
        var builder = new StringBuilder();
        for (var input : testInput) {
            builder.append(input).append(' ');
        }
        var except = builder.toString();
        var actual = tree.toString();

        Assert.isTrue(except.equals(actual), actual);
    }
}