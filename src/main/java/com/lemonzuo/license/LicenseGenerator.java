package com.lemonzuo.license;

/**
 * @author LemonZuo
 * @create 2024-02-20 22:15
 */

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class LicenseGenerator {
    public static final String[] DEFAULT_CODES = {
            "II", "PS", "AC", "DB", "RM", "WS", "RD", "CL", "PC", "GO", "DS", "DC", "DPN", "DM",
            "PSYMFONYPLUGIN", "PWLANG", "PSWPLUGIN", "PGITTOOLBOX", "PHYBRISCOMMERCE", "PMATERIALUI",
            "PSEQUENCEDIAGRA", "PJETFORCER", "PAEMIDE", "PRNCONSOLE", "PANSIHIGHLIGHT", "PYAOQIANGBPMN",
            "PAEM", "PRAINBOWBRACKET", "PGITSCOPE", "PVLOG", "PCODEMRBASE", "PJDCLEANREAD", "PBRWJV",
            "PDB", "PEXTRAICONS", "PBISJ", "PSCIPIO", "PBISAA", "PZENUML", "PJFORMDESIGNER", "PORCHIDE",
            "PIEDIS", "PCMAKEPLUS", "POPENAPI", "PBETTERHIGHLIGH", "PATOMONEDARK", "PGDOC", "POFFICEFLOOR",
            "PWIFIADB", "PLARAVEL", "PODOO", "PCREVIEW", "PMRINTEGEE", "PSFCC", "PMINBATIS", "PPOJOTOJSONSCH",
            "PRDFANDSPARQL", "PBASHSUPPORTPRO", "PMYBATISLOG", "PSMARTJUMP", "PJAVACODESUGG", "PGOLANGCODESUGG",
            "PRUBYCODESUGG", "PVCS", "PJSCODESUGG", "PPHPCODESUGG", "PSVERILOG", "PSPARQL", "PTOOLSET", "PJSONTOTS",
            "PQMLEDITOR", "PSTRKER", "PELASTICSEARCH", "PVISUALGC", "PPYCODESUGG", "PFLUTTER", "PRESTKIT",
            "PAWSLAMBDADEPLR", "PPUMLSTUDIO", "PCWMP", "PFIREHIGHLIGHT", "PJPASQL", "PGODRUNNER", "PLEDGER",
            "PREGEXTOOL", "PAPH", "PGITLABCI", "PCIRCLECI", "PHEROKU", "PREDISMANAGER", "PZEROCODE", "PSTORMSECTIONS",
            "PSENTRYINTEG", "PREDISTOOLS", "PFUZYFIPC", "PBITRISECI", "PQTSQSSEDITOR", "PAPPLETRUNNER", "PDATABASE",
            "PHPEAPLUGIN", "PLEP", "PHPBUILDER", "PMATERIALHC", "PCDMQTTCLIENT", "PISCRATCH", "PRSMGNL", "PCAPELASTIC",
            "PASTOCK", "PCAPREDIS", "PBEANCONVERTER", "PELSA", "PDJANGOTPLPEP", "PQUERYFLAG", "PNGINX", "PKSEXPLORER",
            "PZKA", "PCDAPIRUNNER", "PNEONPRO", "PMBCODEHELPPRO", "PCODEREFACTORAI", "PXSDVISUALIZER", "PSPRINGBOOTIDEA",
            "PEXCELEDITOR", "PGITLAB", "PYAPIQUICKTYPE", "PTERMINAL", "PWIREMOCHA", "PDYNAMODB", "PFASTSHELL", "PJSONNETEMLSUP",
            "PPHPHOUDINI", "POXYXSDJSONSCH", "PQUARKUSHELPER", "PWGCODECREATOR", "PCIINTG", "PDBDATABASETOOL", "PNGROK",
            "PKARATE", "PMATERIALEXTRAS", "PJSONTOANYLANGU", "PMATERIALCUSTOM", "PMATERIALLANG", "PMATERIALFRAME", "PRANCHER",
            "PREDISCLIHELPER", "PSCREENCODEPRO", "PCODEKITS", "PREDISS", "PAWSQLADVISOR", "PLATTEPRO", "PGERRYTHEMESPRO",
            "PUNIAPPSUPPORT", "POPENAPICRUDWIZ", "PGOPARSER", "PNEXTSKETCH", "PNETLIFY", "PGERRYCYBERPUNK", "PTLDRAI", "PBREWBUNDLE",
            "PGERRYSPACE", "PKAFKAIDE", "PGITHUBCI", "PGERRYNATURE", "PEXTENSION", "PSKOL", "PGERRYCHERRY", "PGERRYCOFFEE",
            "PCONNECTUI", "POXYJSONCONVERT", "PDOYTOWIN", "PGERRYAURORA", "PWXUFQYRHZCRSEO", "PWAUFKYVHQCRXEO", "PSQLFLUFFLINTER",
            "PMAGE", "PTAILWINDTOOLS", "PTRAVISCI", "PMONGOEXPERT", "PNEXTSKETCHTWO", "PWXUQQYVOXCRSEO", "PBUILDMON", "PJETCLIENT",
            "PAICODING", "PCAICOMMITAPP", "PCHATGPTCODING", "POLYBPMNGDNEXT", "PARMADILLO", "PVERILOGLANGUAG", "PNOSQLNAVMDB",
            "PCUEFY", "PCOMPOSEHAMMER", "PGPTASSISTANT", "PDTOBUDDY", "PNPMPACKAGEJSON", "PAZURECODING", "PGITLABCICD", "PSENTRY",
            "PKAFKA", "PSRCODEGEN", "PSOURCESYNCPRO", "PAZD", "PWXUQRYTOXCRSEO", "PPOLARISTOMCATS", "PMYBATISFIELDAD", "PIMAGETOVECTOR",
            "PDATAGRAPH", "POXYJSONSCHGEN", "PSPEECHTOTEXT", "PMYSQLPROXY", "PFASTREQUEST", "PMYBATISHELPER", "PREDIS"
    };

    public static void main(String[] args) throws Exception {
        generator();
    }

    private static void generator(String... codes) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(Files.newInputStream(Paths.get("/opt/data/idea_data/jetbrains-license/src/main/resources/cert/ca.crt")));

        // 自己修改 license内容
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 10);
        String date = DateUtil.formatDate(calendar.getTime());
        codes = ArrayUtil.isEmpty(codes) ? DEFAULT_CODES : codes;
        String licenseId = "LemonZuo";
        LicensePart license = new LicensePart(licenseId, codes, date);
        String licensePart = JSONUtil.toJsonStr(license);
        byte[] licensePartBytes = licensePart.getBytes(StandardCharsets.UTF_8);
        String licensePartBase64 = Base64.getEncoder().encodeToString(licensePartBytes);

        PrivateKey privateKey = getPrivateKey();


        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(licensePartBytes);
        byte[] signatureBytes = signature.sign();

        String sigResultsBase64 = Base64.getEncoder().encodeToString(signatureBytes);
        // Combine results as needed
        String result = licenseId + "-" + licensePartBase64 + "-" + sigResultsBase64 + "-" + Base64.getEncoder().encodeToString(cert.getEncoded());
        System.out.println(result);
    }


    static PrivateKey getPrivateKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser pemParser = new PEMParser(new FileReader(FileUtil.getAbsolutePath("/opt/data/idea_data/jetbrains-license/src/main/resources/cert/ca.key")));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
        return kp.getPrivate();
    }

}
