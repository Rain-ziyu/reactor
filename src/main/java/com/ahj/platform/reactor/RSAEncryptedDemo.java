package com.ahj.platform.reactor;

import javax.crypto.Cipher;
import java.security.*;
import java.util.HexFormat;

public class RSAEncryptedDemo {

    public static void main(String[] args) throws Exception {
        // 明文:
        byte[] plain = "Hello, encrypt use RSA".getBytes("UTF-8");
        // 创建公钥／私钥对:
        PersonRSA alice = new PersonRSA("Alice");
        // 用Alice的公钥加密:
        byte[] pk = alice.getPublicKey();
        System.out.println("public key: " + HexFormat.of().formatHex(pk));
        byte[] encrypted = alice.encrypt(plain);
        System.out.println("encrypted: " + HexFormat.of().formatHex(encrypted));
        // 用Alice的私钥解密:
        byte[] sk = alice.getPrivateKey();
        System.out.println("private key: " + HexFormat.of().formatHex(sk));
        byte[] decrypted = alice.decrypt(encrypted);
        System.out.println(new String(decrypted, "UTF-8"));
        // 用Alice的私钥加密:
        byte[] encryptBysk = alice.encryptBysk(plain);
        System.out.println("encrypted: " + HexFormat.of().formatHex(encryptBysk));
        // 用Alice的公钥解密:
        byte[] decryptedpk = alice.decryptBypk(encryptBysk);
        System.out.println(new String(decryptedpk, "UTF-8"));
    }
}

class PersonRSA {
    String name;
    // 私钥:
    PrivateKey sk;
    // 公钥:
    PublicKey pk;

    public PersonRSA(String name) throws GeneralSecurityException {
        this.name = name;
        // 生成公钥／私钥对:
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
        kpGen.initialize(1024);
        KeyPair kp = kpGen.generateKeyPair();
        this.sk = kp.getPrivate();
        this.pk = kp.getPublic();
    }

    // 把私钥导出为字节
    public byte[] getPrivateKey() {
        return this.sk.getEncoded();
    }

    // 把公钥导出为字节
    public byte[] getPublicKey() {
        return this.pk.getEncoded();
    }

    // 用公钥加密:
    public byte[] encrypt(byte[] message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.pk);
        return cipher.doFinal(message);
    }

    // 用私钥解密:
    public byte[] decrypt(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.sk);
        return cipher.doFinal(input);
    }
    // 用私钥加密:
    public byte[] encryptBysk(byte[] message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.sk);
        return cipher.doFinal(message);
    }

    // 用私钥解密:
    public byte[] decryptBypk(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.pk);
        return cipher.doFinal(input);
    }

}
