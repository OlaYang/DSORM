package com.meiqi.data.engine.functions.weka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.engine.weka.ArffUtils;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.util.LogUtil;

import weka.associations.Apriori;
import weka.associations.AprioriItemSet;
import weka.core.FastVector;
import weka.core.Instances;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: 
 * Date: 14-1-20
 * Time: 上午11:21
 */
public class _WEKA_APRIORI extends Function {
    public static final String NAME = _WEKA_APRIORI.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 5) {
            throw new ArgsCountError(NAME);
        }

        final String serviceName = DataUtil.getServiceName(args[0]);
        final String options = DataUtil.getStringValue(args[1]);
        final String attributeKey = DataUtil.getStringValue(args[2]);
        final String attributeValue = DataUtil.getStringValue(args[3]);
        final String input = DataUtil.getStringValue(args[4]);
        final Map<String, Object> param = getParam(args, 5, calInfo.getParam(), false);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, param, NAME);

        __Key key = new __Key(attributeKey, attributeValue, options);
        AprioriResult ret = (AprioriResult) cache.get(key);

        if (ret == null) {
            TService servicePo = Services.getService(serviceName);
            if (servicePo == null) {
                throw new ServiceNotFound(serviceName);
            }

            final D2Data d2Data = Cache4D2Data.getD2Data(servicePo, param,
                    calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), NAME);
            try {
                HashMap<String, LinkedHashMap<String, String>> rows = getRows(d2Data, attributeKey, attributeValue);
                ret = new AprioriResult();

                if (rows.size() != 1 && rows.get("").size() != 0) {
                    Apriori apriori = new Apriori();
                    apriori.setOptions(options.split("[ ]+"));
                    Instances instances = new Instances(new StringReader(ArffUtils.rows2Arff4NOMINAL(rows)));
                    apriori.buildAssociations(instances);

                    final FastVector[] rules = apriori.getAllTheRules();
                    final Instances m_instances = apriori.getInstancesNoClass();
                    final int instancesC = m_instances.numInstances();
                    ret.instances = instancesC;

                    if (rules != null && rules.length != 0) {
                        ret.ruleSum = rules[0].size();

                        for (int i = 0; i < ret.ruleSum; i++) {
                            final AprioriItemSet setLeft = (AprioriItemSet) rules[0].elementAt(i);
                            final AprioriItemSet setRight = (AprioriItemSet) rules[1].elementAt(i);
                            int[] m_items_left = setLeft.items();
                            int[] m_items_right = setRight.items();
                            final double supportLeft = instancesC == 0 ? 0D : (setLeft.support() * 1.0D / instancesC);
                            final double supportRight = instancesC == 0 ? 0D : (setRight.support() * 1.0D / instancesC);
                            final double conf = ((Double) rules[2].elementAt(i)).doubleValue();

                            for (int j = 0; j < m_instances.numAttributes(); j++) {
                                if (m_items_left[j] != -1) {
                                    final String left = m_instances.attribute(j).name();

                                    AprioriRule old = ret.rules.get(left);
                                    if (old == null || old.conf < conf) {
                                        for (int k = 0; k < m_instances.numAttributes(); k++) {
                                            if (m_items_right[k] != -1) {
                                                final AprioriRule rule = new AprioriRule();
                                                rule.supportLeft = supportLeft;
                                                rule.supportRight = supportRight;
                                                rule.conf = conf;
                                                rule.right = m_instances.attribute(k).name();

                                                ret.rules.put(left, rule);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                cache.put(key, ret);
            } catch (RengineException re) {
                throw re;
            } catch (Exception e) {
                throw new RengineException(calInfo.getServiceName(), "错误:" + e.getMessage());
            }
        }

        if (input.length() == 0) {
            if (ret.json == null) {
                ret.json = JSON.toJSONString(ret);
            }
            return ret.json;
        } else {
            String result = ret.cache.get(input);

            if (result == null) {
                HashSet<String> set = new HashSet<String>();
                StringBuilder builder = new StringBuilder();

                for (String inputKey : input.split("[, ]+")) {
                    final AprioriRule rule = ret.rules.get(inputKey);
                    if (rule != null) {
                        if (set.add(rule.right)) {
                            builder.append(rule.right).append(',');
                        }
                    }
                }

                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }

                result = builder.toString();
                ret.cache.put(input, result);
            }

            return result;
        }
    }

    private HashMap<String, LinkedHashMap<String, String>> getRows(D2Data data
            , String attributeKey, String attributeValue) throws
            RengineException {
        HashMap<String, LinkedHashMap<String, String>> rows = new HashMap<String, LinkedHashMap<String, String>>();
        LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>();
        rows.put("", columns);

        int indexKey = -1, indexValue = -1;
        for (TServiceColumn columnPo : data.getColumnList()) {
            if (columnPo.getColumnName().equals(attributeKey)) {
                indexKey = columnPo.getColumnIntIndex();
            } else if (columnPo.getColumnName().equals(attributeValue)) {
                indexValue = columnPo.getColumnIntIndex();
            }
        }

        if (indexKey == -1) {
            throw new RengineException(null, "列" + attributeKey + "不存在");
        }

        if (indexValue == -1) {
            throw new RengineException(null, "列" + attributeValue + "不存在");
        }

        Object[][] d2 = data.getData();
        for (int i = 0, size = d2.length; i < size; i++) {
            final String key = DataUtil.getStringValue(d2[i][indexKey]);
            final String value = DataUtil.getStringValue(d2[i][indexValue]);

            if (key.length() != 0 && value.length() != 0) {

                LinkedHashMap<String, String> row = rows.get(key);
                if (row == null) {
                    row = new LinkedHashMap<String, String>();
                    rows.put(key, row);
                }

                row.put(value, "t");
                columns.put(value, "t");
            }
        }

        return rows;
    }

    class __Key {
        Object attributeKey, attributeValue, options;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (attributeKey != null ? !attributeKey.equals(key.attributeKey) : key.attributeKey != null)
                return false;
            if (attributeValue != null ? !attributeValue.equals(key.attributeValue) : key.attributeValue != null)
                return false;
            if (options != null ? !options.equals(key.options) : key.options != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = attributeKey != null ? attributeKey.hashCode() : 0;
            result = 31 * result + (attributeValue != null ? attributeValue.hashCode() : 0);
            result = 31 * result + (options != null ? options.hashCode() : 0);
            return result;
        }

        __Key(Object attributeKey, Object attributeValue, Object options) {
            this.attributeKey = attributeKey;
            this.attributeValue = attributeValue;
            this.options = options;
        }
    }

    class AprioriResult {
        @JSONField(name = "规则数")
        public int ruleSum = 0;
        @JSONField(name = "个体数")
        public int instances = 0;
        @JSONField(name = "规则详情")
        public Map<String, AprioriRule> rules = new HashMap<String, AprioriRule>();

        @JSONField(serialize = false)
        Map<String, String> cache = new HashMap<String, String>();
        @JSONField(serialize = false)
        String json = null;
    }

    class AprioriRule {
        @JSONField(name = "左支持度")
        public double supportLeft = 0D;
        @JSONField(name = "右支持度")
        public double supportRight = 0D;
        @JSONField(name = "置信度")
        public double conf = 0D;
        @JSONField(name = "后项")
        public String right = "";
    }
}
