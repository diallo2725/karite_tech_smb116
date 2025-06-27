package com.melitech.karitetech.model;

import java.util.List;

public class SyncResult<T> {
    private int created;
    private int updated;
    private int failed;
    private List<ResultItem<T>> results;

    // Getters et setters
    public int getCreated() {return created;}
    public int getUpdated() {return updated;}
    public int getFailed() {return failed;}
    public List<ResultItem<T>> getResults() {return results;}

    public int setCreated(int created) {return created;}
    public int setUpdated(int updated) {return updated;}
    public int setFailed(int failed) {return failed;}
    public List<ResultItem<T>> setResults(List<ResultItem<T>> results) {return results;}

    public static class ResultItem<T> {
        private String status;
        private T data;

        // Getters et setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

}



