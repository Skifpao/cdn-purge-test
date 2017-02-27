package com.wtf.domains

enum Domain {

    KNNLAB("l.global-ssl.fastly.net"), KNECT365("p.ssl.fastly.net")

    final String fastlyHost

    Domain(String fastlyHost) {
        this.fastlyHost = fastlyHost
    }

    String toString() {
        name().toLowerCase() + ".com"
    }

}