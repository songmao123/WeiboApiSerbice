package com.modong.service.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 青松 on 2016/8/29.
 */
public class Pinyin4jUtil {

    /**
     * 返回拼音类型：全拼
     */
    public static final String RET_PINYIN_TYPE_FULL = "full";

    /**
     * 返回拼音类型：首字母
     */
    public static final String RET_PINYIN_TYPE_HEADCHAR = "headChar";

    /**
     * 字符串集合转换字符串(逗号分隔)
     *
     * @param stringSet
     * @return
     */
    public static String makeStringByStringSet(Set<String> stringSet) {
        StringBuilder str = new StringBuilder();
        int i = 0;
        for (String s : stringSet) {
            if (i == stringSet.size() - 1) {
                str.append(s);
            } else {
                str.append(s + ",");
            }
            i++;
        }
        return str.toString().toLowerCase();
    }

    public static Set<String> str2Pinyin(String src) {
        return str2Pinyin(src, null);
    }

    /**
     * 字符串转换为拼音
     * @param src	需要转换的字符串
     * @param retType	返回拼音结果类型
     * @return	如果retType为RET_PINYIN_TYPE_FULL，则返回全拼；如果retType为RET_PINYIN_TYPE_HEADCHAR;如果传入其他值，返回全拼
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static Set<String> str2Pinyin(String src, String retType) {
        if (src != null && !src.trim().equalsIgnoreCase("")) {
            char[] srcChar;
            srcChar = src.toCharArray();
            // 汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
            // 输出设置，大小写，音标方式等
            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
            String[][] temp = new String[src.length()][];
            for (int i = 0; i < srcChar.length; i++) {
                char c = srcChar[i];
                try {
                    temp[i] = PinyinHelper.toHanyuPinyinStringArray(
                            srcChar[i], hanYuPinOutputFormat);

                    if(temp[i] == null){//如果str.charAt(i)非汉字，则保持原样
                        temp[i] = new String[] { String.valueOf(srcChar[i]) };
                    }else{
                        //如果retType是RET_PINYIN_TYPE_HEADCHAR，则只取转换后的首字母
                        if(RET_PINYIN_TYPE_HEADCHAR.equalsIgnoreCase(retType)){
                            String[] temptemp = new String[temp[i].length];
                            for(int j = 0 ; j < temp[i].length; j++){
                                temptemp[j] =String.valueOf(temp[i][j].charAt(0));
                            }
                            temp[i] = temptemp;
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }
            String[] pingyinArray = Exchange(temp);
            Set<String> pinyinSet = new HashSet<String>();
            for (int i = 0; i < pingyinArray.length; i++) {
                pinyinSet.add(pingyinArray[i]);
            }
            return pinyinSet;
        }
        return null;
    }

    /**
     * 递归
     *
     * @param strJaggedArray
     * @return
     */
    public static String[] Exchange(String[][] strJaggedArray) {
        String[][] temp = DoExchange(strJaggedArray);
        return temp[0];
    }

    /**
     * 递归
     *
     * @param strJaggedArray
     * @return
     */
    private static String[][] DoExchange(String[][] strJaggedArray) {
        int len = strJaggedArray.length;
        if (len >= 2) {
            int len1 = strJaggedArray[0].length;
            int len2 = strJaggedArray[1].length;
            int newlen = len1 * len2;
            String[] temp = new String[newlen];
            int Index = 0;
            for (int i = 0; i < len1; i++) {
                for (int j = 0; j < len2; j++) {
                    temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
                    Index++;
                }
            }
            String[][] newArray = new String[len - 1][];
            for (int i = 2; i < len; i++) {
                newArray[i - 1] = strJaggedArray[i];
            }
            newArray[0] = temp;
            return DoExchange(newArray);
        } else {
            return strJaggedArray;
        }
    }

    public static void main(String[] args) {
//        String str = "单单芳abc";
//        System.out.println(makeStringByStringSet(str2Pinyin(str, RET_PINYIN_TYPE_FULL)));
//        System.out.println(makeStringByStringSet(str2Pinyin(str, RET_PINYIN_TYPE_HEADCHAR)));
        int rows = 10;
        for (int i=1; i<=rows; i++) {
            StringBuffer buffer = new StringBuffer();
            for (int j=1; j<=rows+1; j++) {
                if (i % 2 == 1) {
                    if (j == rows+1) {
                        buffer.append(i+1+"\n");
                    } else {
                        buffer.append(i + "");
                    }
                } else {
                    if (j == 1) {
                        buffer.append(i + 1 + "");
                    } else if (j==rows+1) {
                        buffer.append(i + "\n");
                    } else {
                        buffer.append(i + "");
                    }
                }
            }
            System.out.print(buffer.toString());
        }
    }

}
