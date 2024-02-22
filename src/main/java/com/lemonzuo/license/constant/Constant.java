package com.lemonzuo.license.constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LemonZuo
 * @create 2024-02-21 8:43
 */
@Slf4j
public class Constant {
    /**
     * 证书存放路径
     */
    public static String PATH;

    static {
        // 首先尝试从系统属性中获取路径
        String customPath = System.getProperty("path");
        if (customPath != null && !customPath.isEmpty()) {
            log.info("系统属性中获取路径: {}", customPath);
            PATH = customPath;
        } else {
            log.info("使用系统设置默认路径");
            // 系统属性中没有指定，根据操作系统设置默认路径
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                PATH = "E:\\idea_data\\jetbrains-license\\src\\main\\resources\\cert";
            } else {
                PATH = "/opt/module/jetbrains-license/cert";
            }
        }
    }
        /**
         * 证书持有者
         */
    public static final String LICENSEE_NAME = "LemonZuo";
    /**
     * 所有 products的code <a href="https://data.services.jetbrains.com/products?fields=code,name,description"></a>
     * 和所有付费插件的code
     * <a href="https://plugins.jetbrains.com/api/searchPlugins?excludeTags=theme&max=500&offset=0&orderBy=downloads&pricingModels=PAID"></a>
     * <a href="https://plugins.jetbrains.com/api/plugins/{id}"></a>
     */
    public static final String[] DEFAULT_CODES = {"RSCLT", "YTWE", "DG", "PS", "CLN", "RRD", "SPA", "CWMR", "DL", "YTD", "DM", "CWML", "DP", "DS", "QA", "EHS", "MPS", "SPP", "TBA", "RDCPPP", "MF", "QDJVM", "DPCLT", "QDCLD", "QDGO", "US", "QDNET", "RFU", "AC", "IIC", "IIE", "HCC", "JCD", "IDES", "DPPS", "PCC", "RC", "RD", "PCE", "IIU", "RSU", "TCC", "RSCHB", "QDJS", "RM", "DLE", "DMCLP", "PCP", "DPK", "RR", "RS", "FL", "FLIJ", "FLL", "FLS", "MPSIIP", "WRS", "WS", "SP", "GO", "CL", "KT", "QDANDC", "QDJVMC", "DMU", "TC", "GW", "QDPHP", "DCCLT", "QDPYC", "QDPY", "HB", "DC", "PANSIHIGHLIGHT", "PEXTRAICONS", "PLARAVEL", "PDB", "PSPRINGBOOTIDEA", "PMYBATISLOG", "PBASHSUPPORTPRO", "PRNCONSOLE", "PJETFORCER", "PHYBRISCOMMERCE", "PORCHIDE", "PFASTREQUEST", "PFLYINSKYZJBZ", "PREDIS", "PDYNAMODB", "PAEM", "PWIFIADB", "PKSEXPLORER", "PMYBATISHELPER", "PWLANG", "PSWPLUGIN", "PCREVIEW", "PELASTICSEARCH", "PODOO", "PGITLAB", "PFIREHIGHLIGHT", "PGITLABCI", "PYAOQIANGBPMN", "PAEMIDE", "PNGINX", "PIEDIS", "PDBDATABASETOOL", "PJFORMDESIGNER", "PNPMPACKAGEJSON", "POPENAPI", "PREDISMANAGER", "PCMAKEPLUS", "PCODEMRBASE", "PFLUTTER", "PQMLEDITOR", "PUNIAPPSUPPORT", "PSI", "PISCRATCH", "PGITSCOPE", "PVLOG", "PRDFANDSPARQL", "PPUMLSTUDIO", "PQTSQSSEDITOR", "PLEP", "PJDCLEANREAD", "PXSDVISUALIZER", "PBEANCONVERTER", "PNEONPRO", "PAWSLAMBDADEPLR", "PGOLANGCODESUGG", "PJSONNETEMLSUP", "PBREWBUNDLE", "PMATERIALHC", "PPHPCODESUGG", "PCIRCLECI", "PREGEXTOOL", "PSMARTJUMP", "PREDISTOOLS", "PJAVACODESUGG", "PPYCODESUGG", "PMRINTEGEE", "PBRWJV", "PSFCC", "PJSCODESUGG", "POLYBPMNGDNEXT", "PHPEAPLUGIN", "PSVERILOG", "PGITHUBCI", "PPOLARISTOMCATS", "PREDISCLIHELPER", "PBISJ", "PCAICOMMITAPP", "PAICODING", "PMATERIALEXTRAS", "PHEROKU", "PMATERIALLANG", "PRUBYCODESUGG", "PCIINTG", "PAWSQLADVISOR", "PMATERIALCUSTOM", "PTAILWINDTOOLS", "POXYJSONCONVERT", "PQUARKUSHELPER", "PMATERIALFRAME", "PSCIPIO", "PGDOC", "PCDMQTTCLIENT", "PCHATGPTCODING", "PLATTEPRO", "PDBSSH", "PSCREENCODEPRO", "PMINBATIS", "PDATABASE", "PGITLABCICD", "PREDISS", "PEXTENSION", "PKAFKAIDE", "PCAPREDIS", "PFUZYFIPC", "PVCS", "PKAFKA", "PCDAPIRUNNER", "PPOJOTOJSONSCH", "PZKA", "PMONGOEXPERT", "PWXUFQYRHZCRSEO", "PLEDGER", "PBITRISECI", "PNOSQLNAVMDB", "PRANCHER", "PSEQDIAORG", "PTRAVISCI", "PWXUQRYTOXCRSEO", "PWIREMOCHA", "PZEROCODE", "PCAPELASTIC", "PASTOCK", "PSPARQL", "PELSA", "PNETLIFY", "PVERILOGLANGUAG", "PAZD", "PSQLFLUFFLINTER", "POFFICEFLOOR", "POXYJSONSCHGEN", "PFEIGNHELPER", "PNEXTSKETCH", "PNEXTSKETCHTWO", "PWAUFKYVHQCRXEO", "PQUERYFLAG", "PCUEFY", "PPHPHOUDINI", "PAPH", "PGODRUNNER", "PSENTRYINTEG", "PVOQAL", "PSPEECHTOTEXT", "PFIREBASE", "PSRCODEGEN", "PWXUQQYVOXCRSEO", "PAZURECODING", "PIMAGETOVECTOR", "PSOURCESYNCPRO", "PRETROFITASSIT", "PCODEREFACTORAI", "PGPTASSISTANT", "PJQEXPRESS", "PCITRIC", "POXYJSONDIAGRAM", "PSCHEMAREGVIEW", "PLIVESCRIPTINGK", "PNFLUTTER", "PSCIPIOMGNL", "DPN", "PGENSETANDSET", "PDATABASEBUDDY", "PBISAA", "PSCIPIOFTL", "PGOPARSER", "PSWISSKITCONVER", "PMYBATISCODE", "PMICRONAUTLAUNC", "PSOTERISECURITY", "PAUTOLOG"};
}
