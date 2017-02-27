package com.service.config

class FastlyCustomConfig {

    static String buildConfig(String errorPage) {
        """
sub vcl_recv {

  if (req.url ~ "^[^?]*(\\?.*)\$") {
      set req.http.X-QS = re.group.1;
  }

#FASTLY recv

  if (req.request != "HEAD" && req.request != "GET" && req.request != "FASTLYPURGE") {
    return(pass);
  }

  return(lookup);
}

sub vcl_fetch {
#FASTLY fetch

  if ((beresp.status == 500 || beresp.status == 503) && req.restarts < 1 && (req.request == "GET" || req.request == "HEAD")) {
    restart;
  }

  if (req.restarts > 0) {
    set beresp.http.Fastly-Restarts = req.restarts;
  }

  if (beresp.http.Set-Cookie) {
    set req.http.Fastly-Cachetype = "SETCOOKIE";
    return(pass);
  }

  if (beresp.http.Cache-Control ~ "private") {
    set req.http.Fastly-Cachetype = "PRIVATE";
    return(pass);
  }

  if (beresp.status == 500 || beresp.status == 503) {
    set req.http.Fastly-Cachetype = "ERROR";
    set beresp.ttl = 1s;
    set beresp.grace = 5s;
    return(deliver);
  }

  if (beresp.http.Expires || beresp.http.Surrogate-Control ~ "max-age" || beresp.http.Cache-Control ~ "(s-maxage|max-age)") {
    # keep the ttl here
  } else {
    # apply the default ttl
    set beresp.ttl = 3701s;
  }

  return(deliver);
}

sub vcl_hit {
#FASTLY hit

  if (!obj.cacheable) {
    return(pass);
  }
  return(deliver);
}

sub vcl_miss {
#FASTLY miss
  return(fetch);
}

sub vcl_deliver {

  if (resp.status == 301) {
    if (req.http.X-QS) {
      if (resp.http.Location ~ "\\?.*\$") {
        set resp.http.Location = regsub(resp.http.Location, "\\?.*\$", req.http.X-QS);
      } else {
        set resp.http.Location = resp.http.Location req.http.X-QS;
      }
    }
  }

#FASTLY deliver
  return(deliver);
}

sub vcl_error {
#FASTLY error

 /* handle 5XXs*/
 if (obj.status >= 500 && obj.status < 600) {


   /* include your HTML response here */
   synthetic {"${errorPage}"};
   return(deliver);
 }


}

sub vcl_pass {
#FASTLY pass
}
"""
    }
}
