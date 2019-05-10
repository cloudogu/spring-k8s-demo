# Setup

```bash
kubectl apply -f deployment/rabc.yaml
helm init --service-account tiller --history-max 200
helm install --name ingress --namespace kube-system stable/nginx-ingress

helm install --name main --values deployment/main.yml helm/spring-k8s-demo
helm install --name one helm/spring-k8s-demo
helm install --name two helm/spring-k8s-demo
helm install --name three helm/spring-k8s-demo

helm install --name prometheus --namespace kube-system stable/prometheus
helm install --name grafana --namespace kube-system stable/grafana

helm repo add loki https://grafana.github.io/loki/charts
helm repo update
helm upgrade --namespace kube-system --install loki loki/loki-stack

kubens kube-system
kubectl apply -f deployment/jaeger-all-in-one-template.yml
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
# jager ui
kubectl port-forward --namespace kube-system $(kubectl get pods --namespace kube-system -l "app=jaeger" -o jsonpath="{.items[0].metadata.name}") 16686
```
