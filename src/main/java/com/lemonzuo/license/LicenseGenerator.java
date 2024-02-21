package com.lemonzuo.license;

/**
 * @author LemonZuo
 * @create 2024-02-20 22:15
 */

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
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
// import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class LicenseGenerator {
    // 所有 products的code https://data.services.jetbrains.com/products?fields=code,name,description
    // 和所有付费插件的code
    // https://plugins.jetbrains.com/api/searchPlugins?excludeTags=theme&max=500&offset=0&orderBy=downloads&pricingModels=PAID
    // https://plugins.jetbrains.com/api/plugins/{id}
    public static final String[] DEFAULT_CODES = {"RSCLT", "YTWE", "DG", "PS", "CLN", "RRD", "SPA", "CWMR", "DL", "YTD", "DM", "CWML", "DP", "DS", "QA", "EHS", "MPS", "SPP", "TBA", "RDCPPP", "MF", "QDJVM", "DPCLT", "QDCLD", "QDGO", "US", "QDNET", "RFU", "AC", "IIC", "IIE", "HCC", "JCD", "IDES", "DPPS", "PCC", "RC", "RD", "PCE", "IIU", "RSU", "TCC", "RSCHB", "QDJS", "RM", "DLE", "DMCLP", "PCP", "DPK", "RR", "RS", "FL", "FLIJ", "FLL", "FLS", "MPSIIP", "WRS", "WS", "SP", "GO", "CL", "KT", "QDANDC", "QDJVMC", "DMU", "TC", "GW", "QDPHP", "DCCLT", "QDPYC", "QDPY", "HB", "DC", "PANSIHIGHLIGHT", "PEXTRAICONS", "PLARAVEL", "PDB", "PSPRINGBOOTIDEA", "PMYBATISLOG", "PBASHSUPPORTPRO", "PRNCONSOLE", "PJETFORCER", "PHYBRISCOMMERCE", "PORCHIDE", "PFASTREQUEST", "PFLYINSKYZJBZ", "PREDIS", "PDYNAMODB", "PAEM", "PWIFIADB", "PKSEXPLORER", "PMYBATISHELPER", "PWLANG", "PSWPLUGIN", "PCREVIEW", "PELASTICSEARCH", "PODOO", "PGITLAB", "PFIREHIGHLIGHT", "PGITLABCI", "PYAOQIANGBPMN", "PAEMIDE", "PNGINX", "PIEDIS", "PDBDATABASETOOL", "PJFORMDESIGNER", "PNPMPACKAGEJSON", "POPENAPI", "PREDISMANAGER", "PCMAKEPLUS", "PCODEMRBASE", "PFLUTTER", "PQMLEDITOR", "PUNIAPPSUPPORT", "PSI", "PISCRATCH", "PGITSCOPE", "PVLOG", "PRDFANDSPARQL", "PPUMLSTUDIO", "PQTSQSSEDITOR", "PLEP", "PJDCLEANREAD", "PXSDVISUALIZER", "PBEANCONVERTER", "PNEONPRO", "PAWSLAMBDADEPLR", "PGOLANGCODESUGG", "PJSONNETEMLSUP", "PBREWBUNDLE", "PMATERIALHC", "PPHPCODESUGG", "PCIRCLECI", "PREGEXTOOL", "PSMARTJUMP", "PREDISTOOLS", "PJAVACODESUGG", "PPYCODESUGG", "PMRINTEGEE", "PBRWJV", "PSFCC", "PJSCODESUGG", "POLYBPMNGDNEXT", "PHPEAPLUGIN", "PSVERILOG", "PGITHUBCI", "PPOLARISTOMCATS", "PREDISCLIHELPER", "PBISJ", "PCAICOMMITAPP", "PAICODING", "PMATERIALEXTRAS", "PHEROKU", "PMATERIALLANG", "PRUBYCODESUGG", "PCIINTG", "PAWSQLADVISOR", "PMATERIALCUSTOM", "PTAILWINDTOOLS", "POXYJSONCONVERT", "PQUARKUSHELPER", "PMATERIALFRAME", "PSCIPIO", "PGDOC", "PCDMQTTCLIENT", "PCHATGPTCODING", "PLATTEPRO", "PDBSSH", "PSCREENCODEPRO", "PMINBATIS", "PDATABASE", "PGITLABCICD", "PREDISS", "PEXTENSION", "PKAFKAIDE", "PCAPREDIS", "PFUZYFIPC", "PVCS", "PKAFKA", "PCDAPIRUNNER", "PPOJOTOJSONSCH", "PZKA", "PMONGOEXPERT", "PWXUFQYRHZCRSEO", "PLEDGER", "PBITRISECI", "PNOSQLNAVMDB", "PRANCHER", "PSEQDIAORG", "PTRAVISCI", "PWXUQRYTOXCRSEO", "PWIREMOCHA", "PZEROCODE", "PCAPELASTIC", "PASTOCK", "PSPARQL", "PELSA", "PNETLIFY", "PVERILOGLANGUAG", "PAZD", "PSQLFLUFFLINTER", "POFFICEFLOOR", "POXYJSONSCHGEN", "PFEIGNHELPER", "PNEXTSKETCH", "PNEXTSKETCHTWO", "PWAUFKYVHQCRXEO", "PQUERYFLAG", "PCUEFY", "PPHPHOUDINI", "PAPH", "PGODRUNNER", "PSENTRYINTEG", "PVOQAL", "PSPEECHTOTEXT", "PFIREBASE", "PSRCODEGEN", "PWXUQQYVOXCRSEO", "PAZURECODING", "PIMAGETOVECTOR", "PSOURCESYNCPRO", "PRETROFITASSIT", "PCODEREFACTORAI", "PGPTASSISTANT", "PJQEXPRESS", "PCITRIC", "POXYJSONDIAGRAM", "PSCHEMAREGVIEW", "PLIVESCRIPTINGK", "PNFLUTTER", "PSCIPIOMGNL", "DPN", "PGENSETANDSET", "PDATABASEBUDDY", "PBISAA", "PSCIPIOFTL", "PGOPARSER", "PSWISSKITCONVER", "PMYBATISCODE", "PMICRONAUTLAUNC", "PSOTERISECURITY", "PAUTOLOG"};

    public static void main(String[] args) throws Exception {
        generator();
    }

    private static void generator(String... codes) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(Files.newInputStream(Paths.get(String.format("%s/ca.crt", Constant.PATH))));

        // 自己修改 license内容
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 10);
        String date = DateUtil.formatDate(calendar.getTime());
        codes = ArrayUtil.isEmpty(codes) ? DEFAULT_CODES : codes;
        String licenseId = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER_LOWER.toUpperCase(), 10);
        String licenseeName = "LemonZuo";
        LicensePart license = new LicensePart(licenseId, licenseeName, codes, date);
        String licensePart = JSONUtil.toJsonStr(license);
        log.info("licensePart: {}", licensePart);
        byte[] licensePartBytes = licensePart.getBytes(StandardCharsets.UTF_8);
        String licensePartBase64 = Base64.encode(licensePartBytes);

        PrivateKey privateKey = getPrivateKey();


        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(licensePartBytes);
        byte[] signatureBytes = signature.sign();

        String sigResultsBase64 = Base64.encode(signatureBytes);
        // Combine results as needed
        String result = licenseId + "-" + licensePartBase64 + "-" + sigResultsBase64 + "-" + Base64.encode(cert.getEncoded());
        log.info("result: {}", result);
    }


    static PrivateKey getPrivateKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser pemParser = new PEMParser(new FileReader(FileUtil.getAbsolutePath(String.format("%s/ca.key", Constant.PATH))));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
        return kp.getPrivate();
    }

}
