package com.mikudream.uuiduploader.utils;

import java.security.SecureRandom;

public class BindCodeGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * 生成6位随机配对码（数字和大写字母组合）
     * @return 6位随机配对码
     */
    public static String generateBindCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        
        return code.toString();
    }
    
    /**
     * 验证配对码格式是否正确
     * @param bindCode 要验证的配对码
     * @return 是否为有效的配对码格式
     */
    public static boolean isValidBindCode(String bindCode) {
        if (bindCode == null || bindCode.length() != CODE_LENGTH) {
            return false;
        }
        
        String validBindCode = bindCode;
        for (char c : validBindCode.toCharArray()) {
            if (!CHARACTERS.contains(String.valueOf(c))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 生成多个配对码，确保不重复
     * @param count 需要生成的配对码数量
     * @return 配对码数组
     */
    public static String[] generateUniqueBindCodes(int count) {
        String[] codes = new String[count];
        for (int i = 0; i < count; i++) {
            codes[i] = generateBindCode();
        }
        return codes;
    }
} 