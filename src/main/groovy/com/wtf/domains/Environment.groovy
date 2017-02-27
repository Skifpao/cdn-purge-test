package com.wtf.domains

enum Environment {

    PROD(""),
//    DEMO("demo"),
//    QA("qa"),
//    CI("ci"),
//    DEV("dev"),
//    LOCAL("local", true)

    public static final String LOCALHOST = "127.0.0.1"

    final String suffix
    final boolean local

    Environment(String suffix, boolean local = false) {
        this.suffix = suffix
        this.local = local
    }


        Environment() {
//        this.suffix = suffix
//        this.local = local
    }

    String dnsHost(Domain domain) {
         domain.fastlyHost
    }

//    String subdomain(String name) {
//        if (name && suffix) {
//            "${name}-${suffix}"
//        } else if (suffix) {
//            suffix
//        } else {
//            name
//        }
//    }
//
//    String fullyQualifiedDomain(SubDomain subDomain, Domain domain) {
//        def hostname = subdomain(subDomain.prefix)
//        if (hostname) {
//            "${hostname}.${domain}"
//        } else {
//            domain
//        }
//    }

    String getHerokuAppName() {
        "cdn-purge-test"
    }
}
