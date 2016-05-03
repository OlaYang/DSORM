package com.meiqi.data.engine.functions.weka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.engine.weka.ArffUtils;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.io.StringReader;
import java.util.*;

/**
 * User: 
 * Date: 14-1-20
 * Time: 上午11:21
 */
public class _WEKA_KMEANS extends Function {
    public static final String NAME = _WEKA_KMEANS.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 4) {
            throw new ArgsCountError(NAME);
        }

        final String serviceName = DataUtil.getServiceName(args[0]);
        final String options = DataUtil.getStringValue(args[1]);
        final String attributes = DataUtil.getStringValue(args[2]);
        final String attribute = DataUtil.getStringValue(args[3]);

        final Map<String, Object> param = getParam(args, 4, calInfo.getParam(), false);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, param, NAME);

        __Key key = new __Key(attributes, options);
        KMeansResult result = (KMeansResult) cache.get(key);

        if (result == null) {
            TService servicePo = Services.getService(serviceName);
            if (servicePo == null) {
                throw new ServiceNotFound(serviceName);
            }

            final D2Data d2Data = Cache4D2Data.getD2Data(servicePo, param,
                    calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), NAME);
            try {
                SimpleKMeans kMeans = new SimpleKMeans();
                kMeans.setOptions(options.split("[ ]+"));
                Instances instances = new Instances(new StringReader(ArffUtils.d2Data2Arff4NUM(d2Data, attributes)));
                kMeans.buildClusterer(instances);

                result = new KMeansResult();
                result.clusterSum = kMeans.numberOfClusters();

                final int[] clusterSizes = kMeans.getClusterSizes();
                final Instances centroids = kMeans.getClusterCentroids();
                final int attLen = centroids.numAttributes();

                for (int i = 0; i < result.clusterSum; i++) {
                    Cluster cluster = new Cluster();
                    cluster.clusterNum = clusterSizes[i];

                    for (int j = 0; j < attLen; j++) {
                        cluster.atts.put(centroids.attribute(j).name(), centroids.instance(i).value(j));
                    }

                    result.clusters.add(cluster);
                }

                Collections.sort(result.clusters);
                cache.put(key, result);
            } catch (RengineException re) {
                throw re;
            } catch (Exception e) {
                throw new RengineException(calInfo.getServiceName(), "错误:" + e.getMessage());
            }
        }

        if (attribute.length() == 0) {
            if (result.json == null) {
                result.json = JSON.toJSONString(result);
            }

            return result.json;
        } else {
            Object ret = result.clusters.size() == 0 ? "" : result.clusters.get(0).atts.get(attribute);
            if (ret == null) {
                return "";
            }

            return ret;
        }
    }

    class __Key {
        Object attributes, options;

        __Key(Object attributes, Object options) {
            this.attributes = attributes;
            this.options = options;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (attributes != null ? !attributes.equals(key.attributes) : key.attributes != null)
                return false;
            if (options != null ? !options.equals(key.options) : key.options != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = attributes != null ? attributes.hashCode() : 0;
            result = 31 * result + (options != null ? options.hashCode() : 0);
            return result;
        }
    }

    class KMeansResult {
        @JSONField(name = "类总数")
        public int clusterSum = 0;
        @JSONField(name = "详情")
        public List<Cluster> clusters = new ArrayList<Cluster>();

        @JSONField(serialize = false)
        String json = null;
    }

    class Cluster implements Comparable<Cluster> {
        @JSONField(name = "个体数")
        public int clusterNum = 0;
        @JSONField(name = "详情")
        public Map<String, Double> atts = new HashMap<String, Double>();

        @Override
        public int compareTo(Cluster o) {
            return o.clusterNum - clusterNum;
        }
    }
}
