package org.nefure.nefurehouse.model.support;

import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 分页信息的简单封装类
 * @author nefure
 * @date 2022/3/28 12:45
 */
@Setter
public class Page<T> {

    private long totalElements;
    /**
     * 页长
     */
    private int size = 1;
    /**
     * 页号（从1开始）
     */
    private int number;
    private List<T> content;

    public Page(long totalElements,int pageNumber,int limit){
        setTotalElements(totalElements);
        setNumber(pageNumber);
        setSize(limit);
        content = Collections.emptyList();
    }

    public void setNumber(int number){
        if(number > 0){
            this.number = number;
        }
    }

    public boolean isLast() {
        return number == getTotalPages();
    }

    public int getTotalPages() {
        return (int) ((totalElements +size -1)/size);
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFirst() {
        return number == 1;
    }

    /**
     * 此页记录数
     */
    public int getNumberOfElements() {
        return content.size();
    }

    public List<T> getContent() {
        return content;
    }

    public boolean isEmpty(){
        return content.isEmpty();
    }
}
