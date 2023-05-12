package top.rrricardo.postcalendarbackend.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class AvlTreeTest {

    @Test
    void TestInsert1() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        var actual = tree.toString();
        Assertions.assertEquals("1 ", actual);
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
        Assertions.assertEquals(expect, actual);
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
        Assertions.assertEquals(expect, actual);
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
        Assertions.assertEquals(expect, actual);
    }

    @Test
    void TestRemove1() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        tree.remove(1);

        var actual = tree.toString();

        Assertions.assertEquals("", actual);
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
        Assertions.assertEquals("1 3 5 ", actual);
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
        var expect = builder.toString();
        var actual = tree.toString();

        Assertions.assertEquals(expect, actual);
    }

    @Test
    void TestIterator1() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        tree.insert(4);
        tree.insert(5);

        var builder = new StringBuilder();
        for(var node : tree) {
            builder.append(node.toString()).append(' ');
        }

        Assertions.assertEquals("1 2 3 4 5 ", builder.toString());
    }

    @Test
    void TestIterator2() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();
        var list = new ArrayList<Integer>();
        var random = new Random();

        for(var i = 0; i < 50; i++) {
            var input = random.nextInt();

            tree.insert(input);
            list.add(input);
        }

        list.sort(Comparator.naturalOrder());

        var i = 0;
        for(var node : tree) {
            Assertions.assertEquals(list.get(i), node);
            i++;
        }
    }

    @Test
    void testSelectRange1() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();

        tree.insert(1);
        tree.insert(3);
        tree.insert(4);
        tree.insert(5);
        tree.insert(7);

        var result = tree.selectRange(2, 5);
        var builder = new StringBuilder();
        for(var item : result) {
            builder.append(item).append(' ');
        }

        Assertions.assertEquals("3 4 5 ", builder.toString());
    }

    @Test
    void testSelectRange2() throws AvlNodeRepeatException {
        var tree = new AvlTree<Integer>();
        var list = new ArrayList<Integer>();
        var random = new Random();

        for(var i = 0; i < 500; i++) {
            var input = random.nextInt();

            tree.insert(input);
            list.add(input);
        }

        list.sort(Comparator.naturalOrder());

        int begin, end;
        begin = list.get(123);
        end = list.get(456);

        var result = list.subList(123, 457);
        var range = tree.selectRange(begin, end);

        for (var i = 0; i < result.size(); i++) {
            Assertions.assertEquals(result.get(i), range.get(i));
        }
    }
}
