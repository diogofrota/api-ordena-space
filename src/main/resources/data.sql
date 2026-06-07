-- Coordenadas simuladas dentro da area monitorada
MERGE INTO gps_posicoes (tablet_satelital, latitude, longitude, capturado_em, observacao)
KEY (tablet_satelital)
VALUES
('80001', -23.561684, -46.625378, TIMESTAMP '2026-06-07 18:40:00', 'simulada dentro da area'),
('80002', -23.562100, -46.624900, TIMESTAMP '2026-06-07 18:42:00', 'simulada dentro da area'),
('80003', -23.560950, -46.626200, TIMESTAMP '2026-06-07 18:44:00', 'simulada dentro da area');

-- Coordenadas simuladas fora da area monitorada
MERGE INTO gps_posicoes (tablet_satelital, latitude, longitude, capturado_em, observacao)
KEY (tablet_satelital)
VALUES
('80004', -23.550520, -46.633308, TIMESTAMP '2026-06-07 18:46:00', 'simulada fora da area'),
('80005', -23.548900, -46.638100, TIMESTAMP '2026-06-07 18:48:00', 'simulada fora da area'),
('80006', -23.545700, -46.640500, TIMESTAMP '2026-06-07 18:50:00', 'simulada fora da area');
