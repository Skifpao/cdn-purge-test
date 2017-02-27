package com.wtf.domains

enum SubDomain {
    KNECT365(''),
    PEOPLE('people'),

    FINANCE('finance'),
    LIFESCIENCE('lifesciences'),
    MARITIME('maritime'),
    ENERGY('energy'),
    TMT('tmt'),
    MARKETING( 'marketing'),
    EBDGROUP('ebdgroup'),
    HR('hr'),
    EDUCATION( 'education'),
    EUROFORUM('euroforum'),
    AUTOMOTIVE('automotive'),
    LAW('law'),
    HEALTHCARE('healthcare'),

    // DEPRECATED - just redirect to TMT
    TECHNOLOGY('technology'),
    TELECOMS('telecoms'),

    final String prefix

    private SubDomain(String prefix) {
        this.prefix = prefix
    }

}