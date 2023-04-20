package org.freeone.k8s.web.knife.sdk;

import java.util.List;

public class TestRequest {

    private List<TestBean> list;

    public List<TestBean> getList() {
        return this.list;
    }

    public void setList(List<TestBean> list) {
        this.list = list;
    }
}
