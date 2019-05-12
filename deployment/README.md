# Setup

* Install helm

```bash
kubectl apply -f deployment/rabc.yaml
helm init --service-account tiller --history-max 200
```

* Install nginx as ingress controller

```bash
helm install --name ingress --namespace kube-system stable/nginx-ingress
```

* Install prometheus and grafana for metrics and alerts

```bash
helm install --name prometheus --namespace kube-system stable/prometheus
helm install --name grafana --namespace kube-system stable/grafana
```

* Install loki for logging

```bash
helm repo add loki https://grafana.github.io/loki/charts
helm repo update
helm upgrade --namespace kube-system --install loki loki/loki-stack
```

* Install jaeger for distributed tracing

```bash
kubens kube-system
kubectl apply -f deployment/jaeger-all-in-one-template.yml
```

* Install kube-metrics-adapter for autoscalling based on prometheus metrics

```bash
helm repo add banzaicloud-stable https://kubernetes-charts.banzaicloud.com
helm repo update
helm install --name kube-metrics-adapter --namespace kube-system --values deployment/kube-metrics-adapter.yml banzaicloud-stable/kube-metrics-adapter
```

* Deploy Sample application

```bash
helm install --name main --values deployment/main.yml helm/spring-k8s-demo
helm install --name one helm/spring-k8s-demo
helm install --name two helm/spring-k8s-demo
helm install --name three helm/spring-k8s-demo
```

## fix loki with jaeger

```bash
kubectl edit --namespace kube-system deployments loki -o yaml
``` 

```yaml
env:
- name: JAEGER_AGENT_HOST
    value: jaeger-agent
- name: JAEGER_AGENT_PORT
    value: "6831"
```

## port-forwards

```bash
# prometheus
kubectl port-forward --namespace kube-system $(kubectl get pods --namespace kube-system -l "app=prometheus,component=server" -o jsonpath="{.items[0].metadata.name}") 9090
# grafana
kubectl port-forward --namespace kube-system $(kubectl get pods --namespace kube-system -l "app=grafama" -o jsonpath="{.items[0].metadata.name}") 3000
# jager ui
kubectl port-forward --namespace kube-system $(kubectl get pods --namespace kube-system -l "app=jaeger" -o jsonpath="{.items[0].metadata.name}") 16686
```

## debug/load container

```bash
kubectl run -i --tty load-generator --image=busybox /bin/sh
```